# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Changed


## [1.1.4] - 2018-05-17
### Changed
- Moshi,Junit dependency update.
- Max 64 chars allowed for common name.

### Added
- Added proper error code validation for download cert request.

## [1.1.3] - 2018-04-26
### Changed
- Create `PKCS#1` private keys by default.

## [1.1.2] - 2018-04-24
### Added
- Added an option to create (en)decrypted cert bundles.
- Password generator for cert download.
- PEM util classes

### Changed
- Misc test cases changes.
- Changed default download password length to `15`.

## [1.1.1] - 2018-04-19
### Added
- Auto retry the cert download, so no more sleep between create and download.
- Auto extract the cert to key, client cert and cacerts PEM files.
- Google java style formatter plugin.

### Changed
- Junit dependency updated to `5.1.1`.
- Misc refactorings.
- Javadoc update.

## [1.1.0] - 2018-04-12
### Added
- Added an option to select client cert alias.

### Changed
- Updated java doc.

### Removed
- Unused error classes.

### Fixed

## [1.0.0] - 2018-04-06
### Added
- Cws client initial release.


[Unreleased]: https://github.com/oneops/certs-client/compare/release-1.1.4...HEAD
[1.1.4]: https://github.com/oneops/certs-client/compare/release-1.1.3...release-1.1.4
[1.1.3]: https://github.com/oneops/certs-client/compare/release-1.1.2...release-1.1.3
[1.1.2]: https://github.com/oneops/certs-client/compare/release-1.1.1...release-1.1.2
[1.1.1]: https://github.com/oneops/certs-client/compare/release-1.1.0...release-1.1.1
[1.1.0]: https://github.com/oneops/certs-client/compare/release-1.0.0...release-1.1.0
[1.0.0]: https://github.com/oneops/certs-client/compare/release-1.0.0...release-1.0.0
