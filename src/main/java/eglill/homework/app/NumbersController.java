package eglill.homework.app;

import eglill.homework.enums.OrderBy;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@RestController
@Validated
public class NumbersController {

    private final CopyOnWriteArrayList<NumberPairWithSum> list = new CopyOnWriteArrayList<>();

    @GetMapping("/sum")
    public NumberPairWithSum getSum(
            @RequestParam @Min(0) @Max(100) Integer number1,
            @RequestParam @Min(0) @Max(100) Integer number2) {
        NumberPairWithSum response = new NumberPairWithSum(number1, number2);
        list.add(response);
        return response;
    }

    @GetMapping("/all")
    public List<NumberPairWithSum> getAll(
            @RequestParam(required = false) @Min(0) @Max(100) Integer number,
            @RequestParam OrderBy order) {
        switch (order) {
            case INCREASING -> Collections.sort(list);
            case DECREASING -> Collections.sort(list, Collections.reverseOrder());
        }

        if (number == null) {
            return list;
        } else {
            return list.stream().
                    filter(numberPairWithSum -> numberPairWithSum.getNumber1() == number ||
                            numberPairWithSum.getNumber2() == number ||
                            numberPairWithSum.getSum() == number).
                    collect(Collectors.toList());
        }
    }
}
