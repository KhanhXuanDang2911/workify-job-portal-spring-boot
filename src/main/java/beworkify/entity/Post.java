package beworkify.entity;

import beworkify.enumeration.StatusPost;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "posts",
    indexes = {
      @Index(name = "idx_posts_title", columnList = "title"),
      @Index(name = "idx_posts_tags", columnList = "tags"),
    })
public class Post extends BaseEntity {
  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String excerpt;

  @Column(columnDefinition = "TEXT")
  private String content;

  @Column(columnDefinition = "TEXT")
  private String contentText;

  @Enumerated(EnumType.STRING)
  private StatusPost status;

  private String thumbnailUrl;

  @Column(columnDefinition = "TEXT")
  private String tags;

  private String slug;

  private Integer readingTimeMinutes;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private CategoryPost category;

  @ManyToOne
  @JoinColumn(name = "user_author_id")
  private User userAuthor;

  @ManyToOne
  @JoinColumn(name = "employer_author_id")
  private Employer employerAuthor;
}
