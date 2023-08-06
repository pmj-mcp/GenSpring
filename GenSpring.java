import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** Call : java GenSpring com.example.myproject NameOfEntityClass
 */
class GenSpring {

	enum Keys{
		PACKAGE,
		DAL,
		ENTITIES,
		REPOSITORIES,
		SERVICES,
		CONTROLLERS,
		MODELS,
		ENTITY,
		ENTITY_LOWER,
		ENTITY_CAMEL,
		LENGTH_100,
		TARGET_DIR
		;
	}

	final private HashMap<Keys,String> entities = new HashMap<>();

	public static void main(String args[]){

		if (args.length<2){
			System.err.println("Usage:");
			System.err.println("  java GenSpring com.example.myproject MyClass [../ExistsingTargetDir]");
			System.exit(0);
		}
		new GenSpring(args[0], args[1], args.length>2 ? args[2] : "./");
	}

	public GenSpring(String fullNameProject, String entity, String targetDir){

		entities.putAll(
			Map.of(
				Keys.PACKAGE, fullNameProject,
				Keys.DAL, "dal",
				Keys.REPOSITORIES, "repositories",
				Keys.SERVICES, "services",
				Keys.CONTROLLERS, "controllers",
				Keys.MODELS, "models",
				Keys.ENTITIES, "entities",
				Keys.ENTITY, entity,
				Keys.ENTITY_LOWER, entity.toLowerCase(),
				Keys.ENTITY_CAMEL, camelCase(entity)
			)
		);
		entities.putAll(
			Map.of(
				Keys.TARGET_DIR, targetDir,
				Keys.LENGTH_100, "100"
			)
		);


		//------------------------------------------------------------------------------------

		System.out.printf("Entity : %s Creating...\n", entity);
	   	createDALDir();
	   	createDALEntitiesDir();
		//------------------------------------------------------------------------------------
	   	create_DAL_Entities_EntityEntity_JavaFile();

		//------------------------------------------------------------------------------------
	   	createDALRepositoriesDir();
		//------------------------------------------------------------------------------------
	   	create_DAL_Repositories_EntityRepository_JavaFile();

		//------------------------------------------------------------------------------------
	   	createServicesDir();
		//------------------------------------------------------------------------------------
	   	create_Services_EntityService_JavaFile();
	   	create_Servives_EntityServiceImpl_JavaFile();

		//------------------------------------------------------------------------------------
		createControllersDir();
		createControllersModelsDir();
		//------------------------------------------------------------------------------------
		create_Controllers_Models_Entity_JavaFile();
		create_Controllers_EntityController_JavaFile();
	}

	private void createDALDir() {
		mkdir(regexReplaceParamsEnum("${TARGET_DIR}/${DAL}", entities));
	}

	private void createDALEntitiesDir() {
		mkdir(regexReplaceParamsEnum("${TARGET_DIR}/${DAL}/${ENTITIES}", entities));
	}

	private void create_DAL_Entities_EntityEntity_JavaFile() {
		writeFile(
	   		regexReplaceParamsEnum("${TARGET_DIR}/${DAL}/${ENTITIES}/${ENTITY}Entity.java", entities),
	   		regexReplaceParamsEnum("""
package ${PACKAGE}.${DAL}.${ENTITIES}; 

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = \"${ENTITY}\")	// Nom de l'entité Spring
@Table(name = \"${ENTITY_LOWER}\")	// Nom de la table en DB
@Getter
@Setter
public class ${ENTITY}Entity {
	@Id		// Primary Key
	@GeneratedValue(strategy = GenerationType.IDENTITY)	//Auto-Incrémentée
	private Integer id;
	
	@Column (
		length = ${LENGTH_100}/*,
		name = "name",	//Permet de spécifier la nom du champ en DB (par def : nom du membre lowercase()-
		unique = true,	//Rend le champs unique en DB (par def: false)
		nullable = true	//Rend le champ nullable zn DB (par def: false)
		*/
	)
	private String name;
}""",
				entities
			)
		);
	}

	private void createDALRepositoriesDir() {
		mkdir(regexReplaceParamsEnum("${TARGET_DIR}/${DAL}/${REPOSITORIES}", entities));
	}

	private void create_DAL_Repositories_EntityRepository_JavaFile() {
		writeFile(
	   		regexReplaceParamsEnum("${TARGET_DIR}/${DAL}/${REPOSITORIES}/${ENTITY}Repository.java", entities),
	   		regexReplaceParamsEnum("""
package ${PACKAGE}.${DAL}.${REPOSITORIES}; 

import ${PACKAGE}.${DAL}.${ENTITIES}.${ENTITY}Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ${ENTITY}Repository extends JpaRepository<${ENTITY}Entity, Integer> {
}""",
				entities
			)
		);
	}

	private void createServicesDir() {
		mkdir(regexReplaceParamsEnum("${TARGET_DIR}/${SERVICES}", entities));
	}

	private void create_Services_EntityService_JavaFile() {
		writeFile(
	   		regexReplaceParamsEnum("${TARGET_DIR}/${SERVICES}/${ENTITY}Service.java", entities),
	   		regexReplaceParamsEnum("""
package ${PACKAGE}.${SERVICES};

import ${PACKAGE}.${DAL}.${ENTITIES}.${ENTITY}Entity;

import java.util.Optional;
import java.util.Collection;

public interface ${ENTITY}Service {
    Collection<${ENTITY}Entity> findAll();
    Optional<${ENTITY}Entity> findOneById(int id);

    void insert(${ENTITY}Entity entity);
}""",
				entities
			)
		);
	}

	private void create_Servives_EntityServiceImpl_JavaFile() {
		writeFile(
	   		regexReplaceParamsEnum("${TARGET_DIR}/${SERVICES}/${ENTITY}ServiceImpl.java", entities),
	   		regexReplaceParamsEnum("""
package ${PACKAGE}.${SERVICES};

import ${PACKAGE}.${DAL}.${ENTITIES}.${ENTITY}Entity;
import ${PACKAGE}.${DAL}.${REPOSITORIES}.${ENTITY}Repository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Collection;

@Service
public class ${ENTITY}ServiceImpl implements ${ENTITY}Service {
    private final ${ENTITY}Repository ${ENTITY_CAMEL}Repository;


    public ${ENTITY}ServiceImpl(${ENTITY}Repository ${ENTITY_CAMEL}Repository) {
        this.${ENTITY_CAMEL}Repository = ${ENTITY_CAMEL}Repository;
    }

    public Collection<${ENTITY}Entity> findAll() {
        return this.${ENTITY_CAMEL}Repository.findAll();
    }

    public Optional<${ENTITY}Entity> findOneById(int id) {
        return this.${ENTITY_CAMEL}Repository.findById(id);
    }

    public void insert(${ENTITY}Entity entity) {
        this.${ENTITY_CAMEL}Repository.save(entity);
    }
}""",
				entities
			)
		);
	}

	private void createControllersDir() {
		mkdir(regexReplaceParamsEnum("${TARGET_DIR}/${CONTROLLERS}", entities));
	}

	private void createControllersModelsDir() {
		mkdir(regexReplaceParamsEnum("${TARGET_DIR}/${CONTROLLERS}/${MODELS}", entities));
	}

	private void create_Controllers_Models_Entity_JavaFile() {
		writeFile(
			regexReplaceParamsEnum("${TARGET_DIR}/${CONTROLLERS}/${MODELS}/${ENTITY}.java", entities),
			regexReplaceParamsEnum("""
package ${PACKAGE}.${CONTROLLERS}.${MODELS};

import ${PACKAGE}.${DAL}.${ENTITIES}.${ENTITY}Entity;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import lombok.Builder;
import lombok.Setter;
import lombok.Getter;

@Builder
@Setter
@Getter
public class ${ENTITY} {

    private Integer id;

    @NotBlank
    @Length(max = ${LENGTH_100})
    private String name;

    public static ${ENTITY} fromEntity(${ENTITY}Entity entity) {
        ${ENTITY}.${ENTITY}Builder builder = new ${ENTITY}.${ENTITY}Builder()
                .id(entity.getId())
                .name(entity.getName());

        return builder.build();
    }

    public ${ENTITY}Entity toEntity() {
        ${ENTITY}Entity entity = new ${ENTITY}Entity();
        entity.setName(getName());
        return entity;
    }
}""",
				entities
			)
		);
	}

	private void create_Controllers_EntityController_JavaFile() {
		writeFile(
				regexReplaceParamsEnum("${TARGET_DIR}/${CONTROLLERS}/${ENTITY}Controller.java", entities),
				regexReplaceParamsEnum("""
package ${PACKAGE}.${CONTROLLERS};

import ${PACKAGE}.${SERVICES}.${ENTITY}Service;
import ${PACKAGE}.${DAL}.${ENTITIES}.${ENTITY}Entity;
import ${PACKAGE}.${CONTROLLERS}.${MODELS}.${ENTITY};

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.annotation.Secured;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Collection;

@RestController
@RequestMapping(path = {"/${ENTITY}s"})
public class ${ENTITY}Controller {
    private final ${ENTITY}Service ${ENTITY_CAMEL}Service;

    public ${ENTITY}Controller(${ENTITY}Service ${ENTITY_CAMEL}Service) {
        this.${ENTITY_CAMEL}Service = ${ENTITY_CAMEL}Service;
    }

    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<${ENTITY}>> getAllAction() {
        Collection<${ENTITY}Entity> response = this.${ENTITY_CAMEL}Service.findAll();

        List<${ENTITY}> ${ENTITY_CAMEL}List = response.stream()
                .map(${ENTITY}::fromEntity)
                .toList();

        return ResponseEntity.ok(${ENTITY_CAMEL}List);
    }

    @GetMapping(path = {"/{id}"})
    public ResponseEntity<${ENTITY}> getOneAction(
            @PathVariable(name = "id") int id
    ) {
        ${ENTITY}Entity entity = this.${ENTITY_CAMEL}Service.findOneById(id)
                .orElseThrow();

        return ResponseEntity.ok(${ENTITY}.fromEntity(entity));
    }

    @PostMapping(path = {"", "/"})
    public ResponseEntity<${ENTITY}> post${ENTITY}Action(
            @Valid @RequestBody ${ENTITY} form
    ) {
        ${ENTITY}Entity entity = form.toEntity();

        this.${ENTITY_CAMEL}Service.insert(entity);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(${ENTITY}.fromEntity(entity));
    }

}""",
				entities
			)
		);
	}

	private String camelCase(String str){
		return str.toLowerCase().charAt(0)+str.substring(1);
	}

	private void mkdir(String path){
    	try{
			Path p = Paths.get(path); 
			if (Files.exists(p)){
				if (!Files.isDirectory(p)){
					System.err.printf("%s exists but is not a directory\n",p);
					System.exit(0);
				}
			} else {
				System.out.printf("%s not exists. Creating...\n",p);
				Files.createDirectory(p);
			}
	    } catch (Exception e){
	    	e.printStackTrace();
	    	System.exit(0);
	    }
    }

	private void writeFile(String path, String content){
    	try{
			Path p = Paths.get(path); 
			if (Files.exists(p)){
				System.out.printf("%s already exists\n",p);
			} else {
				System.out.printf("%s not exists. Creating...\n",p);
				Files.writeString(Paths.get(path),
					content, 
                   	StandardCharsets.UTF_8,
                   	StandardOpenOption.CREATE
                );
			}
	    } catch (IOException e){
	    	e.printStackTrace();
	    	System.exit(0);
	    }
    }

	private String regexReplaceParamsEnum(String template, Map<Keys, String> hashMap) {
		return regexReplaceParams(
			template, 
			hashMap
			.entrySet()
			.stream()
			.collect(Collectors.toMap(entry -> entry.getKey().toString(),entry -> entry.getValue()))
		);
	}

	private String regexReplaceParams(String template, Map<String, String> hashMap) {
		return hashMap
			.entrySet()
			.stream()
			.reduce(
				template, 
				(s, e) -> Pattern
							.compile("[$]\\{"+e.getKey()+"\\}")
							.matcher(s)
							.replaceAll(e.getValue()), 
				(s, s2) -> s
				);
	}

}
