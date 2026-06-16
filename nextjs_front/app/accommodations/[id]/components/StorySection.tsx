import styles from './StorySection.module.css';
import { StayStoryDto } from '../page';

interface StorySectionProps {
  stories: StayStoryDto[];
}

export default function StorySection({ stories }: StorySectionProps) {
  if (!stories || !Array.isArray(stories) || stories.length === 0) return null;

  // orderNum 순서대로 정렬
  const sorted = [...stories].sort((a, b) => a.orderNum - b.orderNum);

  return (
    <section className={styles.section}>
      {sorted.map((story) => (
        <div key={story.id} className={styles.storyItem}>
          {/* 제목 */}
          <h2 className={styles.storyTitle}>
            <span className={styles.orderNum}>
              {String(story.orderNum).padStart(2, '0')}_
            </span>
            {story.title}
          </h2>

          {/* 본문 */}
          <p className={styles.storyContent}>{story.content}</p>

          {/* 이미지 */}
          {story.imageUrl && (
            <div className={styles.imageWrap}>
              <img
                src={`${process.env.NEXT_PUBLIC_SERVER_URL}${story.imageUrl}`}
                alt={story.title}
                className={styles.image}
              />
            </div>
          )}
        </div>
      ))}
    </section>
  );
}
