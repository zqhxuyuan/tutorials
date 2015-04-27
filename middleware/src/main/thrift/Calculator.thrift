namespace java com.zqh.midd.thrift.server

typedef i32 int
service CalculatorService {
        int add(1:int n1, 2:int n2),
}