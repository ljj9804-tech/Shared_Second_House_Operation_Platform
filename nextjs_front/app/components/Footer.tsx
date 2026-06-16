import styles from './Footer.module.css';

export default function Footer() {
  return (
    <footer className={styles.footer}>
      <div className={styles.footerInner}>
        <p className={styles.footerBrand}>세컨하우스</p>
        <p className={styles.footerText}>나만의 두 번째 집을 찾아보세요.</p>
      </div>
    </footer>
  );
}
