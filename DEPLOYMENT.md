# MMOStats Deployment Guide

## GitHub Repository

The MMOStats plugin has been successfully uploaded to GitHub:

**Repository URL**: https://github.com/keepmeside/MMOStats

## Building the Plugin

### Prerequisites

- Java 17 or higher
- Gradle 8.5 or higher (included via Gradle wrapper)

### Build Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/keepmeside/MMOStats.git
   cd MMOStats
   ```

2. Build the plugin:
   ```bash
   ./gradlew shadowJar
   ```

3. The compiled JAR file will be located at:
   ```
   build/libs/MMOStats-1.0.0.jar
   ```

## CI/CD Setup

Due to GitHub App permission restrictions, the automated CI/CD workflow could not be directly pushed to the repository. However, you can manually add the following GitHub Actions workflow to enable automated builds and releases:

### GitHub Actions Workflow

Create `.github/workflows/build-and-release.yml` with the following content:

```yaml
name: Build and Release MMOStats

on:
  push:
    branches: [ main, master ]
    tags: [ 'v*' ]
  pull_request:
    branches: [ main, master ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
      
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Cache Gradle packages
      uses: actions/cache@v3
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-
          
    - name: Make gradlew executable
      run: chmod +x gradlew
      
    - name: Build with Gradle
      run: ./gradlew shadowJar
      
    - name: Upload build artifacts
      uses: actions/upload-artifact@v3
      with:
        name: MMOStats-jar
        path: build/libs/*.jar
        
    - name: Create Release
      if: startsWith(github.ref, 'refs/tags/v')
      uses: softprops/action-gh-release@v1
      with:
        files: build/libs/*.jar
        generate_release_notes: true
        draft: false
        prerelease: false
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}

  test:
    runs-on: ubuntu-latest
    needs: build
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
      
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Cache Gradle packages
      uses: actions/cache@v3
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-
          
    - name: Make gradlew executable
      run: chmod +x gradlew
      
    - name: Run tests
      run: ./gradlew test
      
    - name: Publish test results
      uses: EnricoMi/publish-unit-test-result-action@v2
      if: always()
      with:
        files: build/test-results/**/*.xml
```

### Setting up CI/CD

1. Navigate to your GitHub repository
2. Create the `.github/workflows/` directory
3. Add the `build-and-release.yml` file with the content above
4. Commit and push the changes

### Automated Releases

To create automated releases:

1. Create a new tag with version format `v*` (e.g., `v1.0.1`):
   ```bash
   git tag v1.0.1
   git push origin v1.0.1
   ```

2. The GitHub Actions workflow will automatically:
   - Build the plugin
   - Run tests
   - Create a GitHub release
   - Upload the JAR file as a release asset

## Manual Release Process

If you prefer manual releases:

1. Build the plugin locally:
   ```bash
   ./gradlew shadowJar
   ```

2. Create a new release on GitHub:
   - Go to https://github.com/keepmeside/MMOStats/releases
   - Click "Create a new release"
   - Choose a tag version (e.g., v1.0.1)
   - Upload the JAR file from `build/libs/`
   - Add release notes
   - Publish the release

## Dependencies Note

The plugin is designed to work with optional dependencies:

- **MMOItems**: For functional stat integration
- **HeadDatabase**: For custom head textures

These dependencies are not included in the build due to licensing restrictions. Users who want full functionality should:

1. Install MMOItems and/or HeadDatabase on their server
2. The plugin will automatically detect and integrate with these plugins at runtime

## Server Installation

1. Download the latest JAR from the releases page
2. Place it in your server's `plugins/` directory
3. (Optional) Install MMOItems and HeadDatabase
4. Start/restart your server
5. Configure the plugin via `config.yml` and `messages.yml`

## Support

For issues, feature requests, or contributions, please use the GitHub repository's issue tracker and pull request system.
