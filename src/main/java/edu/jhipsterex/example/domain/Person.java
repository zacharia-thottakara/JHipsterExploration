package edu.jhipsterex.example.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

/**
 * A Person.
 */
@Node
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    private String id;

    @NotNull(message = "must not be null")
    @Size(max = 64)
    @Property("name")
    private String name;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^\\S+@\\S+\\.\\S+$")
    @Property("email")
    private String email;

    @Property("note")
    private String note;

    @Relationship(value = "HAS_SHARES_WITH", direction = Relationship.Direction.INCOMING)
    private Person shares_with;

    @Relationship("HAS_RESPONSIBLE_FOR")
    @JsonIgnoreProperties(value = { "owned_by", "must_dos", "shared_tos" }, allowSetters = true)
    private Set<Event> responsible_fors = new HashSet<>();

    @Relationship("HAS_MUST_DO")
    @JsonIgnoreProperties(value = { "do_next", "dependent_on", "action_of", "action_for" }, allowSetters = true)
    private Set<Step> must_dos = new HashSet<>();

    @Relationship(value = "HAS_", direction = Relationship.Direction.INCOMING)
    @JsonIgnoreProperties(value = { "do_next", "dependent_on", "action_of", "action_for" }, allowSetters = true)
    private Step can_do;

    @Relationship(value = "HAS_SHARED_TO", direction = Relationship.Direction.INCOMING)
    @JsonIgnoreProperties(value = { "owned_by", "must_dos", "shared_tos" }, allowSetters = true)
    private Event can_see;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Person id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Person name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public Person email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNote() {
        return this.note;
    }

    public Person note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Person getShares_with() {
        return this.shares_with;
    }

    public void setShares_with(Person person) {
        this.shares_with = person;
    }

    public Person shares_with(Person person) {
        this.setShares_with(person);
        return this;
    }

    public Set<Event> getResponsible_fors() {
        return this.responsible_fors;
    }

    public void setResponsible_fors(Set<Event> events) {
        if (this.responsible_fors != null) {
            this.responsible_fors.forEach(i -> i.setOwned_by(null));
        }
        if (events != null) {
            events.forEach(i -> i.setOwned_by(this));
        }
        this.responsible_fors = events;
    }

    public Person responsible_fors(Set<Event> events) {
        this.setResponsible_fors(events);
        return this;
    }

    public Person addResponsible_for(Event event) {
        this.responsible_fors.add(event);
        return this;
    }

    public Person removeResponsible_for(Event event) {
        this.responsible_fors.remove(event);
        return this;
    }

    public Set<Step> getMust_dos() {
        return this.must_dos;
    }

    public void setMust_dos(Set<Step> steps) {
        if (this.must_dos != null) {
            this.must_dos.forEach(i -> i.setAction_for(null));
        }
        if (steps != null) {
            steps.forEach(i -> i.setAction_for(this));
        }
        this.must_dos = steps;
    }

    public Person must_dos(Set<Step> steps) {
        this.setMust_dos(steps);
        return this;
    }

    public Person addMust_do(Step step) {
        this.must_dos.add(step);
        return this;
    }

    public Person removeMust_do(Step step) {
        this.must_dos.remove(step);
        return this;
    }

    public Step getCan_do() {
        return this.can_do;
    }

    public void setCan_do(Step step) {
        this.can_do = step;
    }

    public Person can_do(Step step) {
        this.setCan_do(step);
        return this;
    }

    public Event getCan_see() {
        return this.can_see;
    }

    public void setCan_see(Event event) {
        this.can_see = event;
    }

    public Person can_see(Event event) {
        this.setCan_see(event);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Person)) {
            return false;
        }
        return id != null && id.equals(((Person) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Person{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", email='" + getEmail() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
