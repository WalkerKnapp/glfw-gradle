plugins {
	`java-gradle-plugin`
}

group = "com.walker.plugins"
version = "1.0"

gradlePlugin {
	(plugins) {
		register("cmakeLibrary") {
			id = "com.walker.plugins.cmake-library"
			implementationClass = "com.walker.plugins.cmake.CMakeLibraryPlugin"
		}
		register("wrappedLibrary") {
			id = "com.walker.plugins.wrapped-native"
			implementationClass = "com.walker.plugins.cmake.WrappedNativeLibraryPlugin"
		}
	}
}