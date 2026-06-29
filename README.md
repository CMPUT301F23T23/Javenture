# Javenture

Android household inventory management app built with Java, Firebase, and team-based software engineering practices.

Javenture helps users track household items, photos, serial numbers, barcodes, estimated values, tags, and item details in one mobile app.

## Overview

This was a team software engineering project for CMPUT 301. The app focuses on practical inventory workflows, Firebase-backed persistence, authenticated user data, image handling, scanning, sorting/filtering, and UI-tested Android interactions.

## Features

- Firebase Authentication for user login and account workflows
- Add, edit, delete, and view household inventory items
- Firestore-backed item persistence
- Firebase Storage image upload and deletion
- Camera and photo support for item images
- Barcode and serial number scanning workflows
- RecyclerView-based inventory list
- Sorting and filtering with bottom-sheet controls
- Multi-selection item workflows
- Total estimated value calculation
- UI tests for core user flows

## Tech Stack

- Java
- Android Studio
- Firebase Authentication
- Cloud Firestore
- Firebase Storage
- RecyclerView
- Fragments
- ViewModel-style state management
- Repository pattern
- Android UI testing

## Architecture

The app separates UI fragments, adapters, view-model style state, and repository classes for Firebase-backed data access.

Key files include:

- `HouseHoldItemsFragment.java` - main inventory list workflow
- `AddHouseHoldItemFragment.java` - item creation flow
- `EditHouseHoldItemFragment.java` - item editing flow
- `HouseHoldItemRepository.java` - Firestore item persistence
- `ImageRepository.java` - Firebase Storage image workflows
- `AuthenticationService.java` - Firebase Authentication integration
- `BarcodeScanner.java` and `SerialNumberScanner.java` - scanning workflows

## Testing

The project includes Android instrumentation tests for major workflows:

- Add item
- Edit item
- Photo handling
- Barcode / serial scanning
- Sorting and filtering
- Multi-selection
- User profile
- Total estimated value

Test files are located in:

```text
code/app/src/androidTest/java/com/example/javenture/
