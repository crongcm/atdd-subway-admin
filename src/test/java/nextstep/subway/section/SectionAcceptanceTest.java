package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.LineAcceptanceFactory.ID값으로_지하철노선_조회;
import static nextstep.subway.line.LineAcceptanceFactory.지하철노선_생성;
import static nextstep.subway.section.SectionAcceptanceFactory.지하철구간_생성;
import static nextstep.subway.station.StationAcceptanceFactory.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;

    private StationResponse 신창역;
    private StationResponse 서울역;
    private StationResponse 소요산역;
    private LineResponse 일호선;

    @Autowired
    private LineRepository lineRepository;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        소요산역 = 지하철역_생성("소요산역").as(StationResponse.class);
        신창역 = 지하철역_생성("신창역").as(StationResponse.class);
        서울역 = 지하철역_생성("서울역").as(StationResponse.class);
        일호선 = 지하철노선_생성("1호선", 소요산역.getId(), 신창역.getId(), "파란색", 20).as(LineResponse.class);
    }

    /**
     * When 지하철 구간에 새로운 구간을 추가하면
     * Then 노선에 구간을 등록한다.
     */
    @Test
    @Transactional
    void 역_사이에_새로운_역을_등록한다() {
        ExtractableResponse<Response> 일호선_구간 = 지하철구간_생성(일호선.getId(), 소요산역.getId(), 서울역.getId(), 10);

        Line line = lineRepository.findById(일호선.getId())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 노선입니다."));

        assertThat(일호선_구간.statusCode()).isEqualTo(HttpStatus.OK.value());
        line.getSections().forEach(section ->
                assertThat(section.getDistance()).isEqualTo(10)
        );

    }

    /**
     * When 지하철 구간에 상행 종점역이 추가하여 노선이 연장되면
     * Then 신규 상행 구간이 추가된다.
     */
    @Test
    void 새로운_역을_상행_종점으로_등록() {
        StationResponse 신규_상행역 = 지하철역_생성("신규상행역").as(StationResponse.class);
        ExtractableResponse<Response> 상행역_연장구간 = 지하철구간_생성(일호선.getId(), 신규_상행역.getId(), 소요산역.getId(), 5);

        assertThat(상행역_연장구간.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineResponse 일호선_노선 = ID값으로_지하철노선_조회(일호선.getId());

        List<String> stationNames = 일호선_노선.getStations().stream()
                .map(Station::getName)
                .collect(Collectors.toList());

        assertThat(stationNames).contains("신규상행역");

    }
}
