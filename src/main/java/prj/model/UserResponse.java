package prj.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String username;
    private String name;
    private String surname;
    private String password;
    private List<String> permissions = new ArrayList<>();

    public UserResponse(User u){
        this.id = u.getId();
        this.username = u.getUsername();
        this.name = u.getName();
        this.surname = u.getSurname();
        this.password = u.getPassword();
        this.permissions = new ArrayList<>();
        for(Permission p: u.getPermissions()){
            this.permissions.add(p.getID().toString());
        }
    }

    public UserResponse(){}
}
