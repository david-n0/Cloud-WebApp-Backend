package prj.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String surname;

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USERS_PERMISSIONS",
            joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "PERMISSION_ID", referencedColumnName = "ID")
    )
    private List<Permission> permissions = new ArrayList<>();

    public void addPermission(Permission permission) {
        permissions.add(permission);
        permission.getUsers().add(this);
    }

    public User(UserResponse u){
        this.id = u.getId();
        this.username = u.getUsername();
        this.name = u.getName();
        this.surname = u.getSurname();
        this.password = u.getPassword();
        this.permissions = new ArrayList<>();
        for(String p: u.getPermissions()){
            for(Permissions perm: Permissions.values()){
                if(p.equals(perm.toString()))
                    this.permissions.add(new Permission(perm));
            }
        }
    }

    public User(){}
}
