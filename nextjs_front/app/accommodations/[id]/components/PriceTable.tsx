import styles from './PriceTable.module.css';
import { StayAccommodationPriceDto } from '../../page';

interface PriceTableProps {
  monthlyPrice: number;
  prices: StayAccommodationPriceDto[];
}

function formatPriceRange(price: StayAccommodationPriceDto): string {
  const min = price.minMonths;
  const max = price.maxMonths;

  if (max === null) return `${min}개월 이상`;
  return `${min}개월 이상 ${max}개월 미만`;
}

function formatDiscount(discountRate: number): string {
  if (discountRate === 0) return '정가';
  return `${Math.round(discountRate * 100)}% 할인`;
}

export default function PriceTable({ monthlyPrice, prices }: PriceTableProps) {
  if (!prices || prices.length === 0) return null;

  return (
    <section className={styles.section}>
      <h2 className={styles.sectionTitle}>장기 계약 할인</h2>
      <div className={styles.list}>
        {prices.map((price) => (
          <div key={price.id} className={styles.row}>
            <span className={styles.price}>
              월세{' '}
              {Math.round(
                monthlyPrice * (1 - price.discountRate)
              ).toLocaleString()}
              원
            </span>
            <span className={styles.range}>/ {formatPriceRange(price)}</span>
            <span className={styles.discount}>
              {formatDiscount(price.discountRate)}
            </span>
          </div>
        ))}
      </div>
    </section>
  );
}
