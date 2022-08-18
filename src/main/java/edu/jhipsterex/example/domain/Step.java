package edu.jhipsterex.example.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

/**
 * A Step.
 */
@Node
public class Step implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    private String id;

    @NotNull(message = "must not be null")
    @Property("name")
    private String name;

    @Property("note")
    private String note;

    @Relationship(value = "HAS_DEPENDENT_ON", direction = Relationship.Direction.INCOMING)
    private Step do_next;

    @Relationship(value = "HAS_DO_NEXT", direction = Relationship.Direction.INCOMING)
    private Step dependent_on;

    @Relationship(value = "HAS_MUST_DO", direction = Relationship.Direction.INCOMING)
    @JsonIgnoreProperties(value = { "owned_by", "must_dos", "shared_tos" }, allowSetters = true)
    private Event action_of;

    @Relationship(value = "HAS_MUST_DO", direction = Relationship.Direction.INCOMING)
    @JsonIgnoreProperties(value = { "shares_with", "responsible_fors", "must_dos", "can_do", "can_see" }, allowSetters = true)
    private Person action_for;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Step id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Step name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return this.note;
    }

    public Step note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Step getDo_next() {
        return this.do_next;
    }

    public void setDo_next(Step step) {
        this.do_next = step;
    }

    public Step do_next(Step step) {
        this.setDo_next(step);
        return this;
    }

    public Step getDependent_on() {
        return this.dependent_on;
    }

    public void setDependent_on(Step step) {
        this.dependent_on = step;
    }

    public Step dependent_on(Step step) {
        this.setDependent_on(step);
        return this;
    }

    public Event getAction_of() {
        return this.action_of;
    }

    public void setAction_of(Event event) {
        this.action_of = event;
    }

    public Step action_of(Event event) {
        this.setAction_of(event);
        return this;
    }

    public Person getAction_for() {
        return this.action_for;
    }

    public void setAction_for(Person person) {
        this.action_for = person;
    }

    public Step action_for(Person person) {
        this.setAction_for(person);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Step)) {
            return false;
        }
        return id != null && id.equals(((Step) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Step{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
