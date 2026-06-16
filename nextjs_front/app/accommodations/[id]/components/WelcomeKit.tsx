import styles from './WelcomeKit.module.css';
import { FaBook, FaGift } from 'react-icons/fa';

export default function WelcomeKit() {
  return (
    <section className={styles.section}>
      <h2 className={styles.sectionTitle}>웰컴키트와 집 사용 설명서</h2>

      <div className={styles.grid}>
        {/* 집 사용 설명서 */}
        <div className={styles.card}>
          <div className={styles.iconWrap}>
            <FaBook />
          </div>
          <h3 className={styles.cardTitle}>집 사용 설명서</h3>
          <p className={styles.cardDesc}>
            공간의 구조부터 가전 사용법, 주변 맛집, 로컬플레이스 등
            낯선 집이 익숙해지는 정보를 담았습니다.
          </p>
        </div>

        {/* 웰컴키트 */}
        <div className={styles.card}>
          <div className={styles.iconWrap}>
            <FaGift />
          </div>
          <h3 className={styles.cardTitle}>웰컴키트</h3>
          <p className={styles.cardDesc}>
            추억을 남길 수 있는 소정의 선물을 준비했습니다.
          </p>
        </div>
      </div>
    </section>
  );
}