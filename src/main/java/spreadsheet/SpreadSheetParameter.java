package spreadsheet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

public interface SpreadSheetParameter<T> {

    /**
     * 스프레드시트의 이름을 결정하는 메서드입니다.
     */
    String getSpreadSheetName();

    /**
     * 스프레드의 각 셀을 저장하는 데 필요한 정보를 담은 객체 배열입니다.
     * 예를 들어 숙소 정보를 스프레드시트로 만드는 경우, List<Stay>를 넘겨주어 시트를 만들 수 있습니다.
     */
    List<T> getTargets();

    /**
     * 스프레트 시트 각 열의 이름과, 해당 열의 정보를 T 클래스로부터 추출하는 방법을 결정하는 메서드입니다.
     * Map의 Key는 열의 이름을 명시합니다.
     * Map의 Value로는 해당 열에서 필요로 하는 정보를 T 클래스에서 추출하는 Functional Interface입니다.
     * 예를 들어 '숙소 이름' 에 대한 열을 만들고자 하는 경우, LinkedHashMap을 하나 만들고
     * { "숙소 이름", stay -> stay.getName() } 이라는 정보를 Map에 담은 후 반환하면 됩니다.
     */
    LinkedHashMap<String, Function<T, String>> functionPerCellType();

    /**
     * 엑셀에서 '숫자' 타입으로 표기할 셀의 이름을 결정하는 메서드입니다.
     * 해당 메서드에서 사용하는 이름들은 반드시 functionPerCellType()의 key 값에 존재하는 이름이어야 합니다.
     * 해당 셀의 각 값들은 Double로 파싱하는 데에 문제가 없는 값이어야 합니다.
     */
    List<String> getNumericCellNames();

    default List<String> getCellTypes() {
        var cellTypes = new ArrayList<String>();
        functionPerCellType().forEach((key, value) -> cellTypes.add(key));
        return cellTypes;
    }

    default List<Function<T, String>> getFunctions() {
        var functions = new ArrayList<Function<T, String>>();
        functionPerCellType().forEach((key, value) -> functions.add(value));
        return functions;
    }

    default boolean isNumericCellName(String cellName) {
        return getNumericCellNames().contains(cellName);
    }
}
