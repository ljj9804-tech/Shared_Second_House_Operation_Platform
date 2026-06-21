class StayStoryDto {
  final int id;
  final int orderNum;
  final String title;
  final String content;
  final String? imageUrl;

  StayStoryDto({
    required this.id,
    required this.orderNum,
    required this.title,
    required this.content,
    this.imageUrl,
  });

  factory StayStoryDto.fromJson(Map<String, dynamic> json) {
    return StayStoryDto(
      id: json['id'],
      orderNum: json['orderNum'] ?? 0,
      title: json['title'] ?? '',
      content: json['content'] ?? '',
      imageUrl: json['imageUrl'],
    );
  }
}
