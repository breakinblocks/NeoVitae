# Contributing to Neo Vitae

Thank you for your interest in contributing to Neo Vitae! This document outlines how to contribute to the project and the code standards we follow to maintain consistency across the codebase.

## Table of Contents

- [Getting Started](#getting-started)
- [How to Contribute](#how-to-contribute)
- [Code Style Guidelines](#code-style-guidelines)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)
- [Thank You](#thank-you)

---

## Getting Started

### Prerequisites

- **Java 21** (required for NeoForge 1.21.1)
- **Git** for version control
- An IDE (IntelliJ IDEA recommended, Eclipse also works)
- Basic understanding of Minecraft modding with NeoForge

### Setting Up Your Development Environment

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/NeoVitae.git
   cd NeoVitae
   ```
3. **Import into your IDE** as a Gradle project
4. **Run the Gradle setup**:
   ```bash
   ./gradlew build
   ```
5. **Generate run configurations**:
   - IntelliJ: Run `./gradlew genIntellijRuns`
   - Eclipse: Run `./gradlew genEclipseRuns`

### Running the Mod

```bash
./gradlew runClient    # Launch Minecraft client with the mod
./gradlew runServer    # Launch dedicated server
./gradlew runData      # Run data generation
```

---

## How to Contribute

### Reporting Bugs

- Check existing issues to avoid duplicates
- Use the bug report template if available
- Include Minecraft version, NeoForge version, and mod version
- Provide steps to reproduce the issue
- Include crash logs if applicable (use a paste service like GitHub Gist)

### Suggesting Features

- Open an issue describing the feature
- Explain the use case and why it would benefit players
- Be open to discussion about implementation approaches

### Contributing Code

1. Create a feature branch from `main`:
   ```bash
   git checkout -b feature/your-feature-name
   ```
2. Make your changes following the code style guidelines
3. Test your changes in-game
4. Run data generation if you modified recipes, lang files, or models:
   ```bash
   ./gradlew runData
   ```
5. Commit your changes with clear messages
6. Push to your fork and open a Pull Request

---

## Code Style Guidelines

### General Formatting

| Rule | Standard |
|------|----------|
| Indentation | 4 spaces (no tabs) |
| Line length | 120 characters preferred, 150 max |
| Encoding | UTF-8 |
| Line endings | LF (Unix-style) |
| Trailing whitespace | None |

### Java Conventions

#### Braces
Use K&R style (opening brace on same line):
```java
public class Example {
    public void method() {
        if (condition) {
            // code
        } else {
            // code
        }
    }
}
```

#### Naming Conventions

| Type | Convention | Example |
|------|------------|---------|
| Classes | PascalCase | `BloodAltarBlock` |
| Methods | camelCase | `getBloodAmount()` |
| Variables | camelCase | `currentTier` |
| Constants | UPPER_SNAKE_CASE | `public static final int MAX_TIER = 6;` |
| Registry IDs | snake_case | `"blood_altar"`, `"soul_sword"` |
| Resource paths | snake_case | `textures/item/soul_gem_petty.png` |

#### Registry IDs (Important!)

All registry IDs **must** use `snake_case`:
```java
// Correct
BLOCKS.register("blood_altar", BloodAltarBlock::new);
ITEMS.register("soul_gem_petty", () -> new SoulGemItem(...));

// Incorrect - DO NOT use camelCase
BLOCKS.register("bloodAltar", ...);  // Wrong!
ITEMS.register("soulGemPetty", ...); // Wrong!
```

#### Imports
Organize imports in this order:
1. `java.*` / `javax.*`
2. Third-party libraries (com.google, org.apache, etc.)
3. `net.minecraft.*`
4. `net.neoforged.*`
5. `wayoftime.bloodmagic.*`

Remove unused imports and avoid wildcard imports.

#### Class Structure
Organize class members in this order:
1. Static constants
2. Static fields
3. Instance fields
4. Constructors
5. Static methods
6. Instance methods

#### Annotations
Place annotations on their own line:
```java
@Override
@Nonnull
public ItemStack getResult() {
    return result;
}
```

### Registration Patterns

Use the established registration patterns:

```java
// DeferredRegister for blocks
public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(BloodMagic.MODID);

// DeferredHolder for individual registrations
public static final DeferredHolder<Block, MyBlock> MY_BLOCK = BLOCKS.register("my_block", MyBlock::new);

// BlockWithItemHolder for blocks with items
public static final BlockWithItemHolder<MyBlock, BlockItem> MY_BLOCK = BLOCK_REG.register("my_block", MyBlock::new);
```

### Prefix Conventions

| Prefix | Usage |
|--------|-------|
| `BM` | Registry/helper classes (BMBlocks, BMItems, BMTiles) |
| `Item` | Item classes (ItemSigil, ItemRitualDiviner) |
| `Block` | Block classes when disambiguation needed |
| `Tile` / `TileEntity` | Block entity classes (BloodAltarTile) |

### Resource Locations

Use the helper method for resource locations:
```java
// Correct
BloodMagic.rl("textures/gui/altar.png")

// Instead of
ResourceLocation.fromNamespaceAndPath(BloodMagic.MODID, "textures/gui/altar.png")
```

### Data Components (NeoForge 1.21.1)

Use Data Components instead of NBT for item data:
```java
// Storing data
stack.set(BMDataComponents.MY_COMPONENT, value);

// Retrieving data
MyType value = stack.get(BMDataComponents.MY_COMPONENT);
```

---

## Commit Guidelines

### Commit Message Format

```
type: brief description

Optional longer description explaining the change in more detail.
```

### Types

- `feat`: New feature
- `fix`: Bug fix
- `refactor`: Code refactoring (no functional change)
- `docs`: Documentation changes
- `style`: Code style/formatting changes
- `test`: Adding or updating tests
- `chore`: Build process or auxiliary tool changes

### Examples

```
feat: add hellfire forge soul gem display

fix: correct blood altar tier calculation

refactor: consolidate recipe serialization logic

docs: update contribution guidelines
```

---

## Pull Request Process

1. **Ensure your code compiles** without errors:
   ```bash
   ./gradlew build
   ```

2. **Run data generation** if you changed any data-driven content:
   ```bash
   ./gradlew runData
   ```

3. **Test your changes** in-game

4. **Update documentation** if needed (CLAUDE.md, README.md, etc.)

5. **Create a clear PR description** explaining:
   - What changes you made
   - Why you made them
   - How to test them

6. **Be responsive** to feedback and requested changes

7. **Keep PRs focused** - one feature or fix per PR when possible

---

## Project Structure Overview

```
BloodMagic-Continued/
├── src/
│   ├── main/
│   │   ├── java/wayoftime/bloodmagic/
│   │   │   ├── BloodMagic.java          # Main mod class
│   │   │   ├── client/                   # Client-side code
│   │   │   ├── common/                   # Shared code
│   │   │   │   ├── block/                # Block classes
│   │   │   │   ├── blockentity/          # Block entities
│   │   │   │   ├── datacomponent/        # Data components
│   │   │   │   ├── item/                 # Item classes
│   │   │   │   ├── menu/                 # Container menus
│   │   │   │   └── recipe/               # Recipe types
│   │   │   ├── compat/                   # Mod integrations
│   │   │   ├── ritual/                   # Ritual system
│   │   │   └── util/                     # Utilities
│   │   └── resources/
│   │       └── assets/bloodmagic/        # Textures, models, lang
│   ├── datagen/                          # Data generators
│   └── generated/resources/              # Generated data
├── CLAUDE.md                             # Development guide
├── PORTING_STATUS.md                     # Porting progress
└── build.gradle                          # Build configuration
```

---

## Thank You

We sincerely appreciate everyone who takes the time to contribute to Neo Vitae. Whether you're fixing a typo, reporting a bug, suggesting a feature, or implementing new functionality, your contributions make this project better for the entire community.

Open source thrives because of people like you who are willing to share their time, knowledge, and creativity. Every contribution, no matter how small, helps keep Neo Vitae alive and evolving for players to enjoy.

Special thanks to:
- **WayofTime** - Original creator of the Blood Magic mod
- All contributors who have helped port and maintain the mod
- The NeoForge team for their continued development of the modding platform
- The Minecraft modding community for their support and resources

**Happy modding, and may your blood altars never run dry!**

---

*Neo Vitae is an open source project released under the Creative Commons Attribution 4.0 International License. See the LICENSE file for details.*
