package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {
    private Line line;

    @BeforeEach
    void setUp() {
        line = Line.of("신분당선", "bg-red-600",
                Section.of(Station.from("논현역"), Station.from("강남역"), Distance.from(10)));
    }

    @Test
    @DisplayName("구간 생성")
    void createSection() {
        Station upStation = Station.from("논현역");
        Station downStation = Station.from("강남역");
        Distance distance = Distance.from(10);
        Section actual = Section.of(upStation, downStation, distance);

        assertThat(actual.getUpStation()).isEqualTo(upStation);
        assertThat(actual.getDownStation()).isEqualTo(downStation);
        assertThat(actual.getDistance()).isEqualTo(10);
    }

    @Test
    @DisplayName("구간 생성 필수값 검증 (이름, 거리)")
    void createSectionException() {
        Station station = Station.from("강남역");
        Distance distance = Distance.from(10);

        assertThrows(IllegalArgumentException.class, () -> Section.of(null, station, distance));
        assertThrows(IllegalArgumentException.class, () -> Section.of(station, null, distance));
    }

    @Test
    @DisplayName("기존역 사이 길이보다 크거나 같으면 구간 생성 불가능")
    void distance() {
        Station upStation = Station.from("논현역");
        Station downStation = Station.from("신논현역");
        Distance distance = Distance.from(10);
        assertThatThrownBy(() -> line.addSection(Section.of(upStation, downStation, distance)))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("기존역과 상행/하행역이 모두 같으면 구간 생성 불가능")
    void sameSection() {
        Station upStation = Station.from("논현역");
        Station downStation = Station.from("강남역");
        Distance distance = Distance.from(5);
        assertThatThrownBy(() -> line.addSection(Section.of(upStation, downStation, distance)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("기존역과 일치하는 상행역 또는 하행역이 없을경우 구간 생성 불가능")
    void notContainSection() {
        Station upStation = Station.from("신논현역");
        Station downStation = Station.from("판교역");
        Distance distance = Distance.from(5);
        assertThatThrownBy(() -> line.addSection(Section.of(upStation, downStation, distance)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("역과 역 사이에 구간 추가")
    void addSectionBetweenStations() {
        Station upStation = Station.from("논현역");
        Station downStation = Station.from("신논현역");
        Distance distance = Distance.from(5);
        line.addSection(Section.of(upStation, downStation, distance));

        assertThat(line.getStations()).hasSize(3);
        assertThat(line.totalDistance()).isEqualTo(15);
    }

    @Test
    @DisplayName("구간 추가 상행종점 추가")
    void addSectionUpStation() {
        Station upStation = Station.from("신사역");
        Station downStation = Station.from("논현역");
        Distance distance = Distance.from(5);
        line.addSection(Section.of(upStation, downStation, distance));

        assertThat(line.getStations()).hasSize(3);
        assertThat(line.totalDistance()).isEqualTo(15);
    }

    @Test
    @DisplayName("구간 추가 하행종점 추가")
    void addSectionDownStation() {
        Station upStation = Station.from("강남역");
        Station downStation = Station.from("판교역");
        Distance distance = Distance.from(10);
        line.addSection(Section.of(upStation, downStation, distance));

        assertThat(line.getStations()).hasSize(3);
        assertThat(line.totalDistance()).isEqualTo(20);
    }
}
