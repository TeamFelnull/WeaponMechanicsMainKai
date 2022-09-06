// This is a helper method to compile MechanicsCore, WeaponMechanics, and to
// put all the stuff in zip files and such for github release.
tasks.register("buildForSpigotRelease").configure {
    println("Cleaning build directory")
    val folder = file("build")
    folder.deleteRecursively()
    folder.mkdir()

    // todo REMOVE versions.txt WITHIN FEW WEEKS TO ALLOW PEOPLE TO UPDATE
    val mechanicsCoreVersion = project(":BuildMechanicsCore").version.toString()
    val weaponMechanicsVersion = project(":BuildWeaponMechanics").version.toString()
    println("Writing versions.txt")
    val file = file("build/versions.txt")
    file.appendText("MechanicsCore: $mechanicsCoreVersion\n")
    file.appendText("WeaponMechanics: $weaponMechanicsVersion\n")
    file.appendText("WeaponMechanicsResourcePack: 1.1.0\n")

    println("Compile MechanicsCore")
    dependsOn(":BuildMechanicsCore:shadowJar")
    println("Compile WeaponMechanics")
    dependsOn(":BuildWeaponMechanics:shadowJar")

    finalizedBy("zipForSpigotRelease", "resourcePackForSpigotRelease")
}

tasks.register<Copy>("resourcePackForSpigotRelease") {
    println("Copy resource pack")
    val resourcePackVersion = "1.1.0"
    from("${layout.projectDirectory}\\resourcepack\\WeaponMechanicsResourcePack-${resourcePackVersion}.zip")
    into(layout.buildDirectory)
}

tasks.register<Zip>("zipForSpigotRelease") {
    println("Generate zip file")
    archiveFileName.set("WeaponMechanics.zip")
    destinationDirectory.set(layout.buildDirectory)

    from (layout.buildDirectory) {
        include("*.jar")
    }
}