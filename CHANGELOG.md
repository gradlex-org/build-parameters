# Build Parameters Gradle plugin - Changelog

## Version 1.4
* [New] [#7](https://github.com/gradlex-org/build-parameters/issues/7) ':parameters' help task
* [New] [#25](https://github.com/gradlex-org/build-parameters/issues/25) Mandatory parameters that give an actionable error if not set

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
