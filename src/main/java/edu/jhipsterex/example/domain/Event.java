package edu.jhipsterex.example.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
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
 * A Event.
 */
@Node
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    private String id;

    @NotNull(message = "must not be null")
    @Size(max = 64)
    @Property("name")
    private String name;

    @NotNull(message = "must not be null")
    @Property("initial")
    private LocalDate initial;

    @Property("repeat")
    private String repeat;

    @Property("note")
    private String note;

    @Relationship(value = "HAS_RESPONSIBLE_FOR", direction = Relationship.Direction.INCOMING)
    @JsonIgnoreProperties(value = { "shares_with", "responsible_fors", "must_dos", "can_do", "can_see" }, allowSetters = true)
    private Person owned_by;

    @Relationship("HAS_MUST_DO")
    @JsonIgnoreProperties(value = { "do_next", "dependent_on", "action_of", "action_for" }, allowSetters = true)
    private Set<Step> must_dos = new HashSet<>();

    @Relationship("HAS_SHARED_TO")
    @JsonIgnoreProperties(value = { "shares_with", "responsible_fors", "must_dos", "can_do", "can_see" }, allowSetters = true)
    private Set<Person> shared_tos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Event id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Event name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getInitial() {
        return this.initial;
    }

    public Event initial(LocalDate initial) {
        this.setInitial(initial);
        return this;
    }

    public void setInitial(LocalDate initial) {
        this.initial = initial;
    }

    public String getRepeat() {
        return this.repeat;
    }

    public Event repeat(String repeat) {
        this.setRepeat(repeat);
        return this;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getNote() {
        return this.note;
    }

    public Event note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Person getOwned_by() {
        return this.owned_by;
    }

    public void setOwned_by(Person person) {
        this.owned_by = person;
    }

    public Event owned_by(Person person) {
        this.setOwned_by(person);
        return this;
    }

    public Set<Step> getMust_dos() {
        return this.must_dos;
    }

    public void setMust_dos(Set<Step> steps) {
        if (this.must_dos != null) {
            this.must_dos.forEach(i -> i.setAction_of(null));
        }
        if (steps != null) {
            steps.forEach(i -> i.setAction_of(this));
        }
        this.must_dos = steps;
    }

    public Event must_dos(Set<Step> steps) {
        this.setMust_dos(steps);
        return this;
    }

    public Event addMust_do(Step step) {
        this.must_dos.add(step);
        return this;
    }

    public Event removeMust_do(Step step) {
        this.must_dos.remove(step);
        return this;
    }

    public Set<Person> getShared_tos() {
        return this.shared_tos;
    }

    public void setShared_tos(Set<Person> people) {
        if (this.shared_tos != null) {
            this.shared_tos.forEach(i -> i.setCan_see(null));
        }
        if (people != null) {
            people.forEach(i -> i.setCan_see(this));
        }
        this.shared_tos = people;
    }

    public Event shared_tos(Set<Person> people) {
        this.setShared_tos(people);
        return this;
    }

    public Event addShared_to(Person person) {
        this.shared_tos.add(person);
        return this;
    }

    public Event removeShared_to(Person person) {
        this.shared_tos.remove(person);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        return id != null && id.equals(((Event) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Event{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", initial='" + getInitial() + "'" +
            ", repeat='" + getRepeat() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
