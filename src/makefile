# Makefile for Android Calculator App

# Variables
GRADLE = ./gradlew
ADB = adb
APP_ID = com.example.distributioncalculator
DEVICE = $(shell adb devices | grep -v devices | grep device | cut -f 1)

# Default task
.PHONY: help
help:
	@echo "Available commands:"
	@echo "  make build          - Build debug APK"
	@echo "  make install        - Install debug APK on connected device"
	@echo "  make run            - Run app on connected device"
	@echo "  make uninstall      - Uninstall app from connected device"
	@echo "  make clean          - Clean build files"
	@echo "  make test           - Run unit tests"
	@echo "  make lint           - Run lint checks"
	@echo "  make bundle         - Create release bundle"
	@echo "  make logcat         - Show logcat output"
	@echo "  make devices        - List connected devices"

# Build the debug APK
.PHONY: build
build:
	$(GRADLE) assembleDebug

# Install the debug APK on a connected device
.PHONY: install
install:
	$(GRADLE) installDebug

# Run the app on a connected device
.PHONY: run
run: install
	adb devices
	$(ADB) shell am start -n "$(APP_ID)/$(APP_ID).MainActivity"

# Uninstall the app from a connected device
.PHONY: uninstall
uninstall:
	$(ADB) uninstall $(APP_ID)

# Clean build files
.PHONY: clean
clean:
	$(GRADLE) clean

# Run unit tests
.PHONY: test
test:
	$(GRADLE) test

# Run lint checks
.PHONY: lint
lint:
	$(GRADLE) lint

# Create release bundle
.PHONY: bundle
bundle:
	$(GRADLE) bundleRelease

# Show logcat output
.PHONY: logcat
logcat:
	$(ADB) logcat | grep $(APP_ID)

# List connected devices
.PHONY: devices
devices:
	$(ADB) devices


# Emulator
emu-list:
	emulator -list-avds

emu-start:
	# ANDROID_SDK_ROOT=/home/nuno/Android/Sdk emulator -avd Medium_Phone_API_35
	ANDROID_SDK_ROOT=/home/nuno/Android/Sdk emulator -avd Pixel_8_Pro_API_35
	# must be first installed from android studio
	#

edit:
	nvim app/src/main/java/com/example/distributioncalculator/MainActivity.kt


record:
	adb shell screenrecord /sdcard/video.mp4
	# adb pull /sdcard/video.mp4
	# adb shell rm /sdcard/video.mp4
