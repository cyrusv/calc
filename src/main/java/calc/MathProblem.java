package calc;

public class MathProblem {
  String type;
  int first;
  int second;
  int result;

  public MathProblem(String type, int first, int second, int result) {
    this.type = type;
    this.first = first;
    this.second = second;
    this.result = result;
  }

  public String getType() {
    return type;
  }

  public int getFirst() {
    return first;
  }

  public int getSecond() {
    return second;
  }

  public int getResult() {
    return result;
  }
}
