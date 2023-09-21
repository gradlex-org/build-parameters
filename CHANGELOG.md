# Build Parameters Gradle plugin - Changelog

## Version 1.4.4
* [Fixed] [#88](https://github.com/gradlex-org/build-parameters/issues/88) Example for boolean parameters with default false should be setting it to true. Thanks to @timyates.

## Version 1.4.3
* [Fixed] [#100](https://github.com/gradlex-org/build-parameters/issues/100) Parameter validation ignores parameters from the `org.gralde` namespace
* [Fixed] [#93](https://github.com/gradlex-org/build-parameters/issues/93) Generated Enum's `toString()` method returns the un-escaped value

## Version 1.4.2
* [Fixed] [#87](https://github.com/gradlex-org/build-parameters/issues/87) Code generation is locale sensitive and generates invalid code on some locales

## Version 1.4.1
* [Fixed] [#80](https://github.com/gradlex-org/build-parameters/issues/80) Parameter validation fails IDEA sync
* [Fixed] [#78](https://github.com/gradlex-org/build-parameters/issues/78) Parameters task not compatible with configuration cache

## Version 1.4
* [New] [#7](https://github.com/gradlex-org/build-parameters/issues/7) ':parameters' help task
* [New] [#28](https://github.com/gradlex-org/build-parameters/issues/28) Throw error if a non-existing parameter is set via -P and if -D is used to set a parameter
* [New] [#25](https://github.com/gradlex-org/build-parameters/issues/25) Mandatory parameters that give an actionable error if not set
* [New] [#64](https://github.com/gradlex-org/build-parameters/issues/64) Enum parameter values that contain '-' or '.' and values that are Java keywords

## Version 1.3
* [New] [#18](https://github.com/gradlex-org/build-parameters/issues/18) Fail the build when it's running on an unsupported Gradle version
* [New] [#52](https://github.com/gradlex-org/build-parameters/issues/52) Groups should have a description
* [New] [#53](https://github.com/gradlex-org/build-parameters/issues/53) Render descriptions into getters JavaDoc
* [New] [#54](https://github.com/gradlex-org/build-parameters/issues/54) PluginCodeGeneration should be cacheable

## Version 1.2
* [New] [#42](https://github.com/gradlex-org/build-parameters/issues/42) Boolean parameters: empty string maps to 'true' and invalid value fails the build (instead of silently mapping to 'false')
* [New] [#40](https://github.com/gradlex-org/build-parameters/issues/40) Allow defining parameters without configuration action
* [Fixed] [#43](https://github.com/gradlex-org/build-parameters/issues/43) Parameter groups cannot be used in settings files

## Version 1.1
* [New] [#24](https://github.com/gradlex-org/build-parameters/issues/24) Build parameters are also available in settings files

## Version 1.0
* [New] Initial release
