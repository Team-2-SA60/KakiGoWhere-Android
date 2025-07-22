#### Commands for local testing

1. Run all tests (Lint + Software Composition Analysis + Static Code Analysis + Code Coverage)
```
./gradlew build
```

2. SCA (SonaType)
```
./gradlew ossIndexAudit
```

3. Kotlin Lint + Code formatter (ktlint)

Check linting issues
```
./gradlew ktlintCheck 
```
Auto-Correct linting issues
```
./gradlew ktlintFormat 
```

4. Lint (Android Native Lint)
```
./gradlew lint
```

5. Static Code Analysis (Detekt)
```
./gradlew detekt
```

6. Kover (Code Coverage)
```
./gradlew koverHtmlReport
```