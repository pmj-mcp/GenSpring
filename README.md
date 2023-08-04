# GenSpring
Generate Spring Squeleton classes for one Entity
----

It never replaces an existing file nor directory

### call :
java GenSpring *package_name* *entity_name* [*target_dir*]

- package_name : the full name of the package
- entity_name : the name of the entity class (without "Entity")
- target_dir : where dirs and files will be generated (default: current dir)

```cmd
  java GenSpring com.example.myproject EntityName ..\
````