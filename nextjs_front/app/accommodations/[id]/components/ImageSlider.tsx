'use client';

import { useState, useEffect } from 'react';
import styles from './ImageSlider.module.css';

interface ImageSliderProps {
  imageUrl: string;
  name: string;
}

export default function ImageSlider({ imageUrl, name }: ImageSliderProps) {
  const images = imageUrl
  ? imageUrl.split(',').map((url) => `${process.env.NEXT_PUBLIC_BASE_URL}${url.trim()}`)
  : [];
  const [current, setCurrent] = useState(0);

  // 자동 슬라이드 (3초)
  useEffect(() => {
    if (images.length <= 1) return;
    const timer = setInterval(() => {
      setCurrent((prev) => (prev + 1) % images.length);
    }, 3000);
    return () => clearInterval(timer);
  }, [images.length]);

  // 이미지 없을 때
  if (images.length === 0) {
    return (
      <div className={styles.placeholder}>
        <span>이미지 없음</span>
      </div>
    );
  }

  const goPrev = () => setCurrent((prev) => (prev - 1 + images.length) % images.length);
  const goNext = () => setCurrent((prev) => (prev + 1) % images.length);

  return (
    <div className={styles.slider}>
      {/* 이미지 */}
      <img
        src={images[current]}
        alt={`${name} ${current + 1}`}
        className={styles.image}
      />

      {/* 이전/다음 버튼 */}
      {images.length > 1 && (
        <>
          <button className={`${styles.arrow} ${styles.arrowLeft}`} onClick={goPrev}>
            ‹
          </button>
          <button className={`${styles.arrow} ${styles.arrowRight}`} onClick={goNext}>
            ›
          </button>

          {/* 인디케이터 점 */}
          <div className={styles.dots}>
            {images.map((_, i) => (
              <button
                key={i}
                className={`${styles.dot} ${i === current ? styles.dotActive : ''}`}
                onClick={() => setCurrent(i)}
              />
            ))}
          </div>
        </>
      )}
    </div>
  );
}