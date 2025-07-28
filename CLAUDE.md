# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an IntelliJ IDEA plugin project that implements a prank functionality - it hijacks user typing and replaces all input with Lorem Ipsum text. Built from the official JetBrains IntelliJ Platform Plugin Template.

**Plugin Details:**
- Plugin ID: `org.jetbrains.plugins.prank`
- Plugin Name: "Lorem Ipsum Prank Plugin"
- Target Platform: IntelliJ Community 2024.3.6
- Minimum Build: 243 (2024.3)
- Language: Kotlin with Java 21 JVM toolchain

## Essential Commands

### Development
```bash
# Build and run the plugin in a sandbox IDE
./gradlew runIde

# Build the plugin distribution JAR
./gradlew buildPlugin

# Run tests
./gradlew test

# Run code quality checks and tests
./gradlew check

# Clean build
./gradlew clean build
```

### Plugin Installation
The installable JAR is generated at: `build/libs/IntelliJ Platform Plugin Template-2.2.0.jar`
(Note: Due to build configuration, the main JAR keeps the template name despite plugin being renamed)

## Architecture Overview

### Core Components

1. **InputHijacker.kt** - Main prank functionality
   - Extends `ProjectActivity` to initialize on project startup
   - Hijacks `TypedAction` handler to intercept all keyboard input
   - Replaces typed characters with Lorem Ipsum text sequentially
   - Uses `WriteCommandAction` for proper IntelliJ threading compliance

2. **Plugin Resources**
   - `lorem-ipsum.txt` - Source text for character replacement
   - `plugin.xml` - Plugin configuration and extension registration

### Threading Requirements

IntelliJ requires specific threading patterns for document modifications:
- All document changes must be wrapped in `WriteCommandAction.runWriteCommandAction()`
- This ensures proper undo/redo support and command tracking
- The InputHijacker demonstrates this pattern for editor modifications

### Extension Points Used
- `postStartupActivity` - For plugin initialization via InputHijacker
- `toolWindow` - For UI components (from template)

## Build Configuration

### Gradle Setup
- **Build Tool**: Gradle 8.14.3 with Kotlin DSL
- **Key Plugin**: IntelliJ Platform Gradle Plugin 2.7.0
- **JVM Toolchain**: Java 21
- **Dependencies**: Managed via version catalog in `gradle/libs.versions.toml`

### Important Gradle Properties
```properties
pluginGroup = org.jetbrains.plugins.prank
pluginName = Lorem Ipsum Prank Plugin
pluginVersion = 2.2.0
platformType = IC (IntelliJ Community)
platformVersion = 2024.3.6
```

### JAR Naming Issue
The build configuration has a quirk where the main JAR file retains the template name despite plugin renaming. The actual plugin name appears correctly in the IDE but the JAR filename needs manual adjustment for distribution.

## Testing Framework

- Uses `BasePlatformTestCase` for IntelliJ plugin testing
- Test data stored in `src/test/testData/`
- Code coverage via Kover plugin
- Integration with GitHub Actions for CI/CD

## Key Development Patterns

### Plugin Lifecycle
1. Plugin loads on project startup via `ProjectActivity` interface
2. `InputHijacker.execute()` method runs during project initialization
3. Lorem Ipsum text is loaded from resources
4. `TypedAction` handler is hijacked to intercept all typing

### Resource Loading
```kotlin
val inputStream = javaClass.classLoader.getResourceAsStream("lorem-ipsum.txt")
val text = InputStreamReader(inputStream).readText()
```

### Document Modification Pattern
```kotlin
WriteCommandAction.runWriteCommandAction(editor.project) {
    val document = editor.document
    document.insertString(offset, charToInsert.toString())
    caretModel.moveToOffset(offset + 1)
}
```

## Deployment Notes

The plugin generates three JAR variants:
- `IntelliJ Platform Plugin Template-2.2.0.jar` - Main installable plugin
- `IntelliJ Platform Plugin Template-2.2.0-instrumented.jar` - Debug version
- `Lorem Ipsum Prank Plugin-2.2.0-base.jar` - Base version (correct naming)

For installation in IntelliJ IDEA, use the main JAR file via "Install Plugin from Disk" option.