package exercises.lottery.domain;

import com.fasterxml.jackson.annotation.JsonView;
import exercises.lottery.views.CheckedTicketView;
import exercises.lottery.views.UncheckedTicketView;
import org.springframework.data.annotation.Id;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Class which represents lottery tickets.
 *
 * Created by guisil on 24/07/2016.
 */
public class Ticket {

    @Id
    @JsonView({UncheckedTicketView.class, CheckedTicketView.class})
    private String id;

    @JsonView({UncheckedTicketView.class, CheckedTicketView.class})
    private boolean checked;

    @JsonView({UncheckedTicketView.class, CheckedTicketView.class})
    private Set<Line> lines;


    public Ticket() {
        lines = new LinkedHashSet<>();
    }

    public Ticket(Set<Line> lines) {
        this.lines = lines;
    }

    public Ticket(boolean checked, Set<Line> lines) {
        this.checked = checked;
        this.lines = lines;
    }

    public Ticket(String id, boolean checked, Set<Line> lines) {
        this.id = id;
        this.checked = checked;
        this.lines = lines;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Set<Line> getLines() {
        return lines;
    }

    public void setLines(Set<Line> lines) {
        this.lines = lines;
    }

    public void addLines(Set<Line> newLines) {
        lines.addAll(newLines);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Ticket)) {
            return false;
        }
        Ticket other = (Ticket) obj;

        boolean idIsNull = false;
        if (id == null) {
            if (other.getId() != null) {
                return false;
            }
            idIsNull = true;
        }

        return (idIsNull ? idIsNull : id.equals(other.getId()))
                && other.isChecked() == checked
                && other.getLines().equals(lines);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + id.hashCode();
        result = 31 * result + Boolean.hashCode(checked);
        result = 31 * result + lines.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder();
        representation.append("ID: ").append(id).append((", "));
        representation.append("Checked: ").append(checked).append(", ");
        representation.append("Lines: ").append(lines);
        return representation.toString();
    }
}
