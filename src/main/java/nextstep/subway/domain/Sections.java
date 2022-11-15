package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;

public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSection(Section addSection) {
        validDuplicateSection(addSection);
        sections.add(addSection);
    }

    private void validDuplicateSection(Section compareSection) {
        if (isContainsAllStation(compareSection)) {
            throw new IllegalArgumentException();
        }
    }

    private boolean isContainsAllStation(Section compareSection) {
        return sections.stream().map(Section::stations)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet())
                .containsAll(compareSection.stations());
    }

    public List<Station> getStations() {
        return sections.stream()
                .map(Section::stations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public int totalDistance() {
        return sections.stream().mapToInt(Section::getDistance).sum();
    }
}
