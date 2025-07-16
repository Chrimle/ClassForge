# ClassForge

*Forge Java Classes!*

> Imagine generating [DTO](https://en.wikipedia.org/wiki/Data_transfer_object)s with a *[Builder pattern](https://en.wikipedia.org/wiki/Builder_pattern)*, which functions like a *Database-changeset*.
>
> Only specify the *changes*, and a new DTO will be generated for each set of changes.
> 
> All previous versions of the DTOs could be generated, *or not*, for backwards-compatibility.
> 
> *[Semantic Versioning](https://semver.org/)* (`MAJOR`/`MINOR`/`PATCH`) can be determined automatically based on the changes.
> Or it can be set manually.
> 
> Where applicable, migration strategies can be provided for migrating from one DTO to another DTO.
> Converters may be generated based on these migration strategies.
> 
> ---
> *No more copy-and-pasting entire DTOs when a field is added/removed!*

# Instructions

## Import the dependency

```xml
<dependency>
    <groupId>io.github.chrimle</groupId>
    <artifactId>class-forge</artifactId>
    <version>0.0.0</version>
</dependency>
```
## Example Usages

All types of classes are generated via the [`ClassForge`-class](src/main/java/io/github/chrimle/classforge/ClassForge.java).
Refer to the JavaDocs for further explanations and examples.


