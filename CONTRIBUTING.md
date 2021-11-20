# Contributing Guidelines

Thank you for your interest in contributing to our project. Whether it's a bug report, new feature, correction, or additional documentation, we greatly value feedback and contributions from our community.

Please read through this document before submitting any issues or pull requests to ensure we have all the necessary information to effectively respond to your bug report or contribution.


## Reporting Bugs/Feature Requests

We welcome you to use the GitHub issue tracker to report bugs or suggest features.

When filing an issue, please check [existing open](https://github.com/instriker/WCResidentEvilDBG-Android/issues), or [recently closed](https://github.com/instriker/WCResidentEvilDBG-Android/issues?utf8=%E2%9C%93&q=is%3Aissue%20is%3Aclosed%20), issues to make sure somebody else hasn't already
reported the issue. Please try to include as much information as you can. Details like these are incredibly useful:

* A reproducible test case or series of steps
* The version of our code being used
* Any modifications you've made relevant to the bug
* Anything unusual about your environment or deployment


## Contributing via Pull Requests

Contributions via pull requests are much appreciated. Before sending us a pull request, please ensure that:

1. You are working against the latest source on the *master* branch.
2. You check existing open, and recently merged, pull requests to make sure someone else hasn't addressed the problem already.
3. You open an issue to discuss any significant work - we would hate for your time to be wasted.

To send us a pull request, please:

1. Fork the repository.
2. Modify the source; please focus on the specific change you are contributing. If you also reformat all the code, it will be hard for us to focus on your change.
3. Ensure local tests pass when they exists. It's encouraged to write new tests for your new features, but not mandatory at the moment because of the actual state of the project.
4. Commit to your fork using clear commit messages.
5. Send us a pull request, answering any default questions in the pull request interface.

GitHub provides additional document on [forking a repository](https://help.github.com/articles/fork-a-repo/) and
[creating a pull request](https://help.github.com/articles/creating-a-pull-request/).

### What should I know before I get started?

This project started as an experimental projects targeting the use of  [MVVM design pattern](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel). It's then has been used for other experiments, and therefore you may find many parts of the projects not following industry best practices.

It's still encouraged to improve the project (adding unit tests for instance), as long as we keep the core project uniformity.

Original project contributors have low times to spend on this project now, so we expect contributors to know Android Development and have a good level of autonomy.

### Need Help?

If you have project specitic questions, you can contact the project team at [support.redbgc@instriker.net](mailto:support.redbgc@instriker.net).

If you have general Android development questions, try general development forums first. Original projects developers, having low time to spend on this project anymore, will most likely slow you down.

### Styleguides

There is no explicit Styleguides. Simply follow project uniformity.

### Local development

Local development is done using Android Studio on Windows 10.

You are free to use an other environment as best fit you as long as it doesn't impact other contributers.

### Local testing

Since there are not automated tests, we have to do manual testing for the project. We expect that changes are tested at least on:

* The oldest supported version of Android (minSdkVersion)
* The target supported version of Android (targetSdkVersion)
* The latest supported version of Android (targetSdkVersion)
* On a phone screen size
* On a tablet screen size

Note: You can usually test the non-platform specific code on a single platform, and then validate regression once done to save your time.

## Finding contributions to work on
Looking at the existing issues is a great way to find something to contribute on. As our projects, by default, use the default GitHub issue labels ((enhancement/bug/duplicate/help wanted/invalid/question/wontfix), looking at any ['help wanted'](https://github.com/instriker/WCResidentEvilDBG-Android/labels/help%20wanted) issues is a great place to start.

## Code of Conduct
This project and everyone participating in it is governed by the [Code of Conduct](./CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior to support.redbgc@instriker.net.

## Security issue notifications
If you discover a potential security issue in this project we ask that you notify by contacting the project team at [support.redbgc@instriker.net](mailto:support.redbgc@instriker.net). Please do **not** create a public github issue.


## Licensing

See the [LICENSE](https://github.com/instriker/WCResidentEvilDBG-Android/blob/master/LICENSE.md) file for our project's licensing. We will ask you to confirm the licensing of your contribution.
