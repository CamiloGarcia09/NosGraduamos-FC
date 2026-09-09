---
name: unit-test-validator
description: Usa esta skill después de crear, completar o modificar pruebas unitarias en el proyecto MessageUcoLab, para auditar su calidad real antes de darlas por terminadas — no basta con que compilen y pasen. Detecta asserts triviales, tests vacíos o deshabilitados, mocking indebido de infraestructura real en tests de core, excepciones genéricas en vez de la jerarquía de dominio, violaciones de la arquitectura hexagonal, y verifica que la cobertura JaCoCo reportada sea real y no inflada. Úsala también si el usuario pide "revisa/valida/audita las pruebas que se crearon".
license: Apache-2.0
---

# Validación de pruebas unitarias

Objetivo: confirmar que las pruebas creadas prueban comportamiento real, respetan
la arquitectura hexagonal del proyecto y que la cobertura reportada es confiable
— no solo que `./mvnw test` pase en verde.

Aplica esta validación a TODA prueba nueva o modificada antes de reportarla como
terminada. Si algo falla, no la marques como válida: corrígela o repórtala como
pendiente con la razón exacta.

## 1. Verificación de ejecución y cobertura

```bash
./mvnw clean verify
```

- Confirma BUILD SUCCESS en los 3 módulos (`utils`, `core`, `infrastructure`).
- Abre el reporte JaCoCo de cada módulo (`target/site/jacoco/index.html` o
  `jacoco.xml`) y confirma que la cobertura de **líneas Y ramas** (branches) sea
  ≥80% — no valides solo el porcentaje agregado del proyecto, valida por módulo.
- Sospecha si un archivo pasó de 0% a 100% con muy pocos tests: revisa que no sea
  cobertura "de paso" (el código se ejecuta pero no se verifica nada relevante).

## 2. Detección de asserts triviales o inexistentes

Busca patrones que indican que el test no verifica comportamiento real:

```bash
grep -rn "assertTrue(true)\|assertFalse(false)" --include="*Test.java" src/test
grep -rn "@Disabled\|@Ignore" --include="*Test.java" src/test
grep -rn "// TODO\|fail(\"not implemented" --include="*Test.java" src/test
```

Reglas de rechazo:
- Un `@Test` cuyo único assert es `assertNotNull(objeto)` sin verificar ningún
  valor/campo/comportamiento específico → **rechazar**.
- Un `@Test` que solo verifica que "no lanza excepción" sin comprobar el
  resultado → **rechazar**, salvo que el propósito explícito del test sea ese
  (ej. verificar que un caso válido no lance error de validación) Y además
  verifique el resultado retornado.
- Cualquier `@Disabled`/`@Ignore` sin un comentario que justifique por qué
  (ticket, bug conocido) → **rechazar**.
- Un método de test cuyo cuerpo está vacío o solo tiene un comentario →
  **rechazar**.

## 3. Verificación de arquitectura hexagonal (específico de este proyecto)

Para tests en `core/src/test/**`:
```bash
grep -rln "@SpringBootTest\|org.springframework" core/src/test
grep -rln "SurrealDB\|RedisTemplate\|PulsarClient" core/src/test
```
Si alguno de estos comandos devuelve resultados → **rechazar**. Los tests de
`core` deben ser JUnit 5 + Mockito puro, mockeando puertos (interfaces), nunca
frameworks ni clientes concretos.

Para tests en `infrastructure/src/test/**`:
- Verifica que los tests unitarios (no de integración) mockeen los clientes
  externos (SurrealDB client, `RedisTemplate`, `PulsarClient`, LangChain4j model)
  en vez de instanciarlos reales o requerir `docker compose up`.
- Si un test de infraestructura requiere la infraestructura real corriendo,
  confirma que esté correctamente separado como test de integración (no
  mezclado con la suite unitaria que corre en cada build).

## 4. Verificación de manejo de excepciones

```bash
grep -rn "assertThrows(RuntimeException.class\|assertThrows(Exception.class" --include="*Test.java" src/test
```
Si aparece, **rechazar**: el test debe verificar la excepción específica de la
jerarquía de dominio (`BusinessException`, `ValidationException`,
`ConflictException`, `NotFoundException`, `TechnicalException` o su subclase
concreta), nunca la clase genérica.

Para validadores (`*RuleImpl`), confirma que:
- Cada regla tiene su propia clase de test, no solo se prueba indirectamente a
  través del `CompositeValidator`.
- El `CompositeValidator` tiene al menos un test que confirma que agrega
  correctamente los resultados de varias reglas (no solo prueba una regla
  aislada).

## 5. Verificación de estructura y nomenclatura

Por cada clase de test nueva, confirma:
- [ ] Nombre de método describe comportamiento (`metodo_condicion_resultado` o
      `@DisplayName` equivalente) — rechaza nombres genéricos como `test1()`,
      `testMetodo()`.
- [ ] Estructura Arrange-Act-Assert identificable (aunque no esté comentada).
- [ ] Al menos: 1 caso feliz, 1 caso límite/borde, 1 caso de error (si el método
      puede fallar).
- [ ] Sin imports no usados ni código comentado muerto (esto lo marca
      SonarCloud como code smell en el Quality Gate).
- [ ] Sin duplicación evidente con otro test ya existente en la suite.

## 6. Reporte de validación

Al terminar, entrega un veredicto por clase de test revisada, en esta forma:

```
✅ PedidoServiceTest — cumple: AAA, excepciones de dominio, sin mocks indebidos.
❌ ClienteValidatorTest — rechazado: usa assertThrows(Exception.class) en línea 42,
   debe verificar ValidationException específica.
⚠️  PagoRepositoryTest — revisar: mockea RedisTemplate correctamente, pero el
   caso de error solo verifica "no lanza excepción" sin comprobar el resultado.
```

No des por válida ninguna prueba con veredicto ❌. Corrígela tú mismo si tienes
permisos de edición, o repórtala explícitamente como pendiente si no.