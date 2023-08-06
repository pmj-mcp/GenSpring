import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Call : java GenSpring com.example.myproject NameOfEntityClass
 */
class GenSpring {

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

		entities.putAll(Map.of(
			Keys.PACKAGE, fullNameProject,
			Keys.DAL, "dal",
			Keys.REPOSITORIES, "repositories",
			Keys.SERVICES, "services",
			Keys.CONTROLLERS, "controllers",
			Keys.MODELS, "models",
			Keys.ENTITIES, "entities",
			Keys.ENTITY, entity,
			Keys.ENTITY_LOWER, entity.toLowerCase(),
			Keys.LENGTH_100, "100"
	   	));
		entities.putAllMap.of(
			Keys.TARGET_DIR, targetDir,
			Keys.ENTITY_CAMEL, camelCase(entity)
		);


		//------------------------------------------------------------------------------------

		System.out.printf("Entity : %s Creating...\n",entity);
	   	mkdir(String.format("%s/%s",entities.get(Keys.TARGET_DIR),entities.get(Keys.DAL)));
	   	mkdir(String.format("%s/%s/%s",entities.get(Keys.TARGET_DIR),entities.get(Keys.DAL),entities.get(Keys.ENTITIES)));
		//------------------------------------------------------------------------------------
	   	writeFile(
	   		String.format("%s/%s/%s/%sEntity.java",entities.get(Keys.TARGET_DIR), entities.get(Keys.DAL),entities.get(Keys.ENTITIES),entities.get(Keys.ENTITY)),
	   		String.format("""
package %s.%s.%s; 

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = \"%s\")	// Nom de l'entité Spring
@Table(name = \"%s\")	// Nom de la table en DB
@Getter
@Setter
public class %sEntity {
	@Id		// Primary Key
	@GeneratedValue(strategy = GenerationType.IDENTITY)	//Auto-Incrémentée
	private Integer id;
	
	@Column (
		length = %s/*,
		name = "name",	//Permet de spécifier la nom du champ en DB (par def : nom du membre lowercase()-
		unique = true,	//Rend le champs unique en DB (par def: false)
		nullable = true	//Rend le champ nullable zn DB (par def: false)
		*/
	)
	private String name;
}""",
				entities.get(Keys.PACKAGE),
				entities.get(Keys.DAL),
				entities.get(Keys.ENTITIES),
				entities.get(Keys.ENTITY),
				entities.get(Keys.ENTITY_LOWER),
				entities.get(Keys.ENTITY),
				entities.get(Keys.LENGTH_100)
			)
		);

		//------------------------------------------------------------------------------------
	   	mkdir(String.format("%s/%s/%s",entities.get(Keys.TARGET_DIR),entities.get(Keys.DAL),entities.get(Keys.REPOSITORIES)));
		//------------------------------------------------------------------------------------
	   	writeFile(
	   		String.format("%s/%s/%s/%sRepository.java",entities.get(Keys.TARGET_DIR), entities.get(Keys.DAL),entities.get(Keys.REPOSITORIES),entities.get(Keys.ENTITY)),
	   		String.format("""
package %s.%s.%s; 

import %s.%s.%s.%sEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface %sRepository extends JpaRepository<%sEntity, Integer> {
}""",
				entities.get(Keys.PACKAGE),
				entities.get(Keys.DAL),
				entities.get(Keys.REPOSITORIES),
				entities.get(Keys.PACKAGE),	//Import
				entities.get(Keys.DAL),
				entities.get(Keys.ENTITIES),
				entities.get(Keys.ENTITY),
				entities.get(Keys.ENTITY),		//interface ...Repository
				entities.get(Keys.ENTITY)		//<...Entity,
			)
		);

		//------------------------------------------------------------------------------------
	   	mkdir(String.format("%s/%s",entities.get(Keys.TARGET_DIR),entities.get(Keys.SERVICES)));
		//------------------------------------------------------------------------------------
	   	writeFile(
	   		String.format("%s/%s/%sService.java",entities.get(Keys.TARGET_DIR), entities.get(Keys.SERVICES),entities.get(Keys.ENTITY)),
	   		String.format("""
package %s.%s;

import %s.%s.%s.%sEntity;

import java.util.Optional;
import java.util.Collection;

public interface %sService {
    Collection<%sEntity> findAll();
    Optional<%sEntity> findOneById(int id);

    void insert(%sEntity entity);
}""",
				entities.get(Keys.PACKAGE), //Pkg
				entities.get(Keys.SERVICES),
				entities.get(Keys.PACKAGE), //Import
				entities.get(Keys.DAL),
				entities.get(Keys.ENTITIES),
				entities.get(Keys.ENTITY),
				entities.get(Keys.ENTITY),	//Interface
				entities.get(Keys.ENTITY),	//findAll
				entities.get(Keys.ENTITY),	//findonebyid
				entities.get(Keys.ENTITY)	//Insert
			)
		);

		//------------------------------------------------------------------------------------
	   	writeFile(
	   		String.format("%s/%s/%sServiceImpl.java",entities.get(Keys.TARGET_DIR), entities.get(Keys.SERVICES),entities.get(Keys.ENTITY)),
	   		String.format("""
package %s.%s;

import %s.%s.%s.%sEntity;
import %s.%s.%s.%sRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Collection;

@Service
public class %sServiceImpl implements %sService {
    private final %sRepository %sRepository;


    public %sServiceImpl(%sRepository %sRepository) {
        this.%sRepository = %sRepository;
    }

    public Collection<%sEntity> findAll() {
        return this.%sRepository.findAll();
    }

    public Optional<%sEntity> findOneById(int id) {
        return this.%sRepository.findById(id);
    }

    public void insert(%sEntity entity) {
        this.%sRepository.save(entity);
    }
}""",
				entities.get(Keys.PACKAGE), //Pkg
				entities.get(Keys.SERVICES),
				entities.get(Keys.PACKAGE), //Import Entity
				entities.get(Keys.DAL),
				entities.get(Keys.ENTITIES),
				entities.get(Keys.ENTITY),
				entities.get(Keys.PACKAGE), //Import Repository
				entities.get(Keys.DAL),
				entities.get(Keys.REPOSITORIES),
				entities.get(Keys.ENTITY),
				entities.get(Keys.ENTITY),	//Class
				entities.get(Keys.ENTITY),	//Impl
				entities.get(Keys.ENTITY),	//Repository
				entities.get(Keys.ENTITY_CAMEL),
				entities.get(Keys.ENTITY),	//ctor
				entities.get(Keys.ENTITY),
				entities.get(Keys.ENTITY_CAMEL),
				entities.get(Keys.ENTITY_CAMEL),	//init
				entities.get(Keys.ENTITY_CAMEL),
				entities.get(Keys.ENTITY),	//findAll
				entities.get(Keys.ENTITY_CAMEL),	//repository.findAll
				entities.get(Keys.ENTITY),	//findonebyid
				entities.get(Keys.ENTITY_CAMEL),	//repository.findonebyid
				entities.get(Keys.ENTITY),	//Insert
				entities.get(Keys.ENTITY_CAMEL)	//repository.save
			)
		);


		//------------------------------------------------------------------------------------
		mkdir(String.format("%s/%s",entities.get(Keys.TARGET_DIR),entities.get(Keys.CONTROLLERS)));
		mkdir(String.format("%s/%s/%s",entities.get(Keys.TARGET_DIR),entities.get(Keys.CONTROLLERS),entities.get(Keys.MODELS)));
		//------------------------------------------------------------------------------------
		writeFile(
			String.format("%s/%s/%s/%s.java",entities.get(Keys.TARGET_DIR), entities.get(Keys.CONTROLLERS),entities.get(Keys.MODELS),entities.get(Keys.ENTITY)),
			String.format("""
package %s.%s.%s;

import %s.%s.%s.%sEntity;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import lombok.Builder;
import lombok.Setter;
import lombok.Getter;

@Builder
@Setter
@Getter
public class %s {

    private Integer id;

    @NotBlank
    @Length(max = %s)
    private String name;

    public static %s fromEntity(%sEntity entity) {
        %s.%sBuilder builder = new %s.%sBuilder()
                .id(entity.getId())
                .name(entity.getName());

        return builder.build();
    }

    public %sEntity toEntity() {
        %sEntity entity = new %sEntity();
        entity.setName(getName());
        return entity;
    }
}""",
				entities.get(Keys.PACKAGE),	//Pkg
				entities.get(Keys.CONTROLLERS),
				entities.get(Keys.MODELS),
				entities.get(Keys.PACKAGE), //Import Entity
				entities.get(Keys.DAL),
				entities.get(Keys.ENTITIES),
				entities.get(Keys.ENTITY),
				entities.get(Keys.ENTITY),	//Class ...
				entities.get(Keys.LENGTH_100),	//Length(Max=...
				entities.get(Keys.ENTITY),	//... fromEntity(
				entities.get(Keys.ENTITY),	//...Entity
				entities.get(Keys.ENTITY),	//...Builder
				entities.get(Keys.ENTITY),
				entities.get(Keys.ENTITY),
				entities.get(Keys.ENTITY),
				entities.get(Keys.ENTITY),	//...Entity toEntity(
				entities.get(Keys.ENTITY),	//...Entity
				entities.get(Keys.ENTITY)	//...Entity
			)
		);
		//------------------------------------------------------------------------------------
		writeFile(
				String.format("%s/%s/%sController.java",entities.get(Keys.TARGET_DIR), entities.get(Keys.CONTROLLERS),entities.get(Keys.ENTITY)),
				String.format("""
package %s.%s;

import %s.%s.%sService;
import %s.%s.%s.%sEntity;
import %s.%s.%s.%s;

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
@RequestMapping(path = {"/%ss"})
public class %sController {
    private final %sService %sService;

    public %sController(%sService %sService) {
        this.%sService = %sService;
    }

    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<%s>> getAllAction() {
        Collection<%sEntity> response = this.%sService.findAll();

        List<%s> %sList = response.stream()
                .map(%s::fromEntity)
                .toList();

        return ResponseEntity.ok(%sList);
    }

    @GetMapping(path = {"/{id}"})
    public ResponseEntity<%s> getOneAction(
            @PathVariable(name = "id") int id
    ) {
        %sEntity entity = this.%sService.findOneById(id)
                .orElseThrow();

        return ResponseEntity.ok(%s.fromEntity(entity));
    }

    @PostMapping(path = {"", "/"})
    public ResponseEntity<%s> post%sAction(
            @Valid @RequestBody %s form
    ) {
        %sEntity entity = form.toEntity();

        this.%sService.insert(entity);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(%s.fromEntity(entity));
    }

}""",
				entities.get(Keys.PACKAGE), //Pkg
				entities.get(Keys.CONTROLLERS),
				entities.get(Keys.PACKAGE), //Import Service
				entities.get(Keys.SERVICES),
				entities.get(Keys.ENTITY),
				entities.get(Keys.PACKAGE), //Import Entity
				entities.get(Keys.DAL),
				entities.get(Keys.ENTITIES),
				entities.get(Keys.ENTITY),
				entities.get(Keys.PACKAGE), //Import Model
				entities.get(Keys.CONTROLLERS),
				entities.get(Keys.MODELS),
				entities.get(Keys.ENTITY),
				entities.get(Keys.ENTITY), //RequestMapping
				entities.get(Keys.ENTITY),	//Class
				entities.get(Keys.ENTITY),	//var Service
				entities.get(Keys.ENTITY_CAMEL),
				entities.get(Keys.ENTITY),	//ctor
				entities.get(Keys.ENTITY),
				entities.get(Keys.ENTITY_CAMEL),
				entities.get(Keys.ENTITY_CAMEL),
				entities.get(Keys.ENTITY_CAMEL),
				entities.get(Keys.ENTITY),	//getAllAction
				entities.get(Keys.ENTITY),
				entities.get(Keys.ENTITY_CAMEL),		//...service
				entities.get(Keys.ENTITY),		//List<...>
				entities.get(Keys.ENTITY_CAMEL),		//...list
				entities.get(Keys.ENTITY),		//map(...
				entities.get(Keys.ENTITY_CAMEL),		//return
				entities.get(Keys.ENTITY),	//getOneAction
				entities.get(Keys.ENTITY),		//...Entity
				entities.get(Keys.ENTITY_CAMEL),		//...service
				entities.get(Keys.ENTITY),		//return
				entities.get(Keys.ENTITY),	//<...> post*Action
				entities.get(Keys.ENTITY),		//post...Action
				entities.get(Keys.ENTITY),		//Param
				entities.get(Keys.ENTITY),		//...Entity
				entities.get(Keys.ENTITY_CAMEL),		//...service
				entities.get(Keys.ENTITY)		//return
			)
		);
	}
}
