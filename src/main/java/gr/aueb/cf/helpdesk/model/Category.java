package gr.aueb.cf.helpdesk.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category extends AbstractEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 7)
    private String color;

    @Column(nullable = false)
    private boolean active = true;
}
