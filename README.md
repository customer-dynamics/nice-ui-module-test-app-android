# CXone Chat UI Module Test App

## Description

This is a testbed project for demoing and testing various features of the [NICE CXone Chat UI Module](https://github.com/nice-devone/nice-cxone-mobile-sdk-android?tab=readme-ov-file#cxone-chat-ui), a drop-in chat interface for Android applications. The project is currently configured for a live chat chat channel, but the implementation is nearly the same for asynchronous messaging channels.

## Items of Interest

### Application.kt

Contains the Koin configuration required for the UI Module. Especially important for configuring logging and custom fields.

### MainActivity.kt

The main activity that launches the chat. A simple example of how you might integrate the UI Module in your app.

### ChatManager.kt

Manages all chat behavior using the SDK and UI Module. Important to reference for all chat behavior, including launching the chat, push notifications, custom fields, theme customization, and custom behavior.

### ContactCustomFieldsProvider.kt

Provides custom fields for the contact (case) to be provided to the UI Module.

### strings.xml

Contains text overrides for elements of the UI module—important for localization.
