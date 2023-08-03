import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Call : java GenSpring com.example.myproject NameOfEntityClass
 */
class GenSpring {

	public static void mkdir(String path){
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

	public static void writeFile(String path, String content){
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


	public static void main(String args[]){
		String pkg = args[0];
		String entity = args[1];
		Map entities = Map.of(
			"package", pkg,
			"dal", "dal",
			"repositories", "repositories",
			"services", "services",
			"controllers", "controllers",
			"models", "models",
			"entities", "entities",
			"Entity", entity,
			"entity", entity.toLowerCase(),
			"100", "?"
	   	);


		//------------------------------------------------------------------------------------

		System.out.printf("Entity : %s Creating...\n",entity);
	   	mkdir(String.format("./%s",entities.get("dal")));
	   	mkdir(String.format("./%s/%s",entities.get("dal"),entities.get("entities")));
		//------------------------------------------------------------------------------------
	   	writeFile(
	   		String.format("./%s/%s/%sEntity.java", entities.get("dal"),entities.get("entities"),entities.get("Entity")),
	   		String.format("""
package %s.%s.%s; 

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = \"%s\")	// Nom de l'entité Spring
@Table(name = \"%s\")	// Nom de la table en DB
@Getter
@Setter
Class %s {
	@Id		// Primary Key
	@GeneratedValue(strategy = GeneratedValue.IDENTITY)	//Auto-Incrémentée
	private Integer id;
	
	@Column (
		Length = %s/*,
		name = "name",	//Permet de spécifier la nom du champ en DB (par def : nom du membre lowercase()-
		unique = true,	//Rend le champs unique en DB (par def: false)
		nullable = true	//Rend le champ nullable zn DB (par def: false)
		*/
	)
	private String name;
}""",
				entities.get("package"),
				entities.get("dal"),
				entities.get("entities"),
				entities.get("Entity"),
				entities.get("Entity"),
				entities.get("entity"),
				entities.get("100")
			)
		);

		//------------------------------------------------------------------------------------
	   	mkdir(String.format("./%s/%s",entities.get("dal"),entities.get("repositories")));
		//------------------------------------------------------------------------------------
	   	writeFile(
	   		String.format("./%s/%s/%sRepository.java", entities.get("dal"),entities.get("repositories"),entities.get("Entity")),
	   		String.format("""
package %s.%s.%s; 

import %s.%s.%s.%sEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface %sRepository extends JpaRepository<%sEntity, Integer> {
}""",
				entities.get("package"),
				entities.get("dal"),
				entities.get("repositories"),
				entities.get("package"),
				entities.get("dal"),
				entities.get("entities"),
				entities.get("Entity"),
				entities.get("Entity"),
				entities.get("Entity"),
				entities.get("entity"),
				entities.get("Entity"),
				entities.get("Entity"),
				entities.get("Entity")
			)
		);

		//------------------------------------------------------------------------------------
	   	mkdir(String.format("./%s",entities.get("services")));
		//------------------------------------------------------------------------------------
	   	writeFile(
	   		String.format("./%s/%sService.java", entities.get("services"),entities.get("Entity")),
	   		String.format("""
package %s.%s;

import %s.%s.%s.%sEntity;

import java.util.Optional;
import java.util.Colletction;

public interface %sService {
    Collection<%sEntity> findAll();
    Optional<%sEntity> findOneById(int id);

    void insert(%sEntity entity);
}""",
				entities.get("package"), //Pkg
				entities.get("services"),
				entities.get("package"), //Import
				entities.get("dal"),
				entities.get("entities"),
				entities.get("Entity"), 
				entities.get("Entity"),	//Interface
				entities.get("Entity"),	//findAll
				entities.get("entity"),	//findonebyid
				entities.get("Entity")	//Insert
			)
		);

		//------------------------------------------------------------------------------------
	   	writeFile(
	   		String.format("./%s/%sServiceImpl.java", entities.get("services"),entities.get("Entity")),
	   		String.format("""
package %s.%s;

import %s.%s.%s.%sEntity;
import %s.%s.%s.%sRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Colletction;

@Service
public class %sServiceImpl implements %sService {
    private final %sRepository %sRepository;


    public %sServiceImpl(%sRepository %sRepository) {
        this.%sRepository = %sRepository;
    }

    public Collection<%sEntity> findAll() {
        return this.%sRepository.findAll());
    }

    public Optional<%sEntity> findOneById(int id) {
        return this.%sRepository.findById(id);
    }

    public void insert(%sEntity entity) {
        this.%sRepository.save(entity);
    }
}""",
				entities.get("package"), //Pkg
				entities.get("services"),
				entities.get("package"), //Import Entity
				entities.get("dal"),
				entities.get("entities"),
				entities.get("Entity"), 
				entities.get("package"), //Import Repository
				entities.get("dal"),
				entities.get("entities"),
				entities.get("Entity"), 
				entities.get("Entity"),	//Class
				entities.get("Entity"),	//Impl
				entities.get("Entity"),	//Repository
				entities.get("entity"),	
				entities.get("Entity"),	//ctor
				entities.get("Entity"),	
				entities.get("entity"),	
				entities.get("entity"),	//init
				entities.get("entity"),	
				entities.get("Entity"),	//findAll
				entities.get("entity"),	//repository.findAll
				entities.get("Entity"),	//findonebyid
				entities.get("entity"),	//repository.findonebyid
				entities.get("Entity"),	//Insert
				entities.get("entity")	//repository.save
			)
		);


		//------------------------------------------------------------------------------------
		mkdir(String.format("./%s",entities.get("controllers")));
		mkdir(String.format("./%s/%s",entities.get("controllers"),entities.get("models")));
		//------------------------------------------------------------------------------------
		writeFile(
			String.format("./%s/%s/%s.java", entities.get("controllers"),entities.get("models"),entities.get("Entity")),
			String.format("""
package %s.%s.%s;

import %s.%s.%s.%sEntity;
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
                .imma(entity.getName());

        return builder.build();
    }

    public %sEntity toEntity() {
        %sEntity entity = new %sEntity();
        entity.setName(getName());
        return entity;
    }
}""",
				entities.get("package"),	//Pkg
				entities.get("controllers"),
				entities.get("models"),
				entities.get("package"), //Import Entity
				entities.get("dal"),
				entities.get("entities"),
				entities.get("Entity"), 
				entities.get("Entity"),	//Class ...
				entities.get("100"),	//Length(Max=...
				entities.get("Entity"),	//... fromEntity(
				entities.get("Entity"),	//...Entity
				entities.get("Entity"),	//...Builder
				entities.get("Entity"),
				entities.get("Entity"),
				entities.get("Entity"),
				entities.get("Entity"),	//...Entity toEntity(
				entities.get("Entity"),	//...Entity
				entities.get("Entity")	//...Entity
			)
		);
		//------------------------------------------------------------------------------------
		writeFile(
				String.format("./%s/%sController.java", entities.get("controllers"),entities.get("Entity")),
				String.format("""
package %s.%scontrollers;

import %s.%s.%sService;
import %s.%s.%s.%sEntity;
import %s.%s.%s.%s;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
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
				entities.get("package"), //Pkg
				entities.get("controllers"),
				entities.get("package"), //Import Service
				entities.get("services"),
				entities.get("Entity"),
				entities.get("package"), //Import Entity
				entities.get("dal"),
				entities.get("entities"),
				entities.get("Entity"), 
				entities.get("package"), //Import Model
				entities.get("controllers"),
				entities.get("models"),
				entities.get("Entity"), 
				entities.get("Entity"), //RequestMapping
				entities.get("Entity"),	//Class
				entities.get("Entity"),	//var Service
				entities.get("entity"),	
				entities.get("Entity"),	//ctor
				entities.get("Entity"),	
				entities.get("entity"),	
				entities.get("entity"),	
				entities.get("entity"),	
				entities.get("Entity"),	//getAllAction
				entities.get("Entity"),	
				entities.get("entity"),		//...service
				entities.get("Entity"),		//List<...>
				entities.get("entity"),		//...list
				entities.get("Entity"),		//map(...
				entities.get("entity"),		//return
				entities.get("Entity"),	//getOneAction
				entities.get("Entity"),		//...Entity
				entities.get("entity"),		//...service
				entities.get("Entity"),		//return
				entities.get("Entity"),	//<...> post*Action
				entities.get("Entity"),		//post...Action
				entities.get("Entity"),		//Param
				entities.get("Entity"),		//...Entity
				entities.get("entity"),		//...service
				entities.get("Entity")		//return
			)
		);



	}
}



