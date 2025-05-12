namespace py randomservice
namespace java com.example.randomservice

typedef i64 Timestamp

struct RandomNumberStruct {
    1: optional i32 randomNumber,
    2: optional Timestamp createdAt,
}

struct Range {
    1: i32 start,
    2: i32 end,
}

exception InvalidRandomRange {
    1: optional string desc,
}

service RandomService {
  /** Return a random integer among the range */
  RandomNumberStruct roll(1: Range range) throws (1: InvalidRandomRange invalidRandomRange);
}
