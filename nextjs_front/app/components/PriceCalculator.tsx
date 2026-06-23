import styles from './PriceCalculator.module.css';
import { MONTH_OPTIONS } from '@/app/lib/constants';

interface PriceCalculatorProps {
  teams: number;
  months: number;
  onTeamsChange: (value: number) => void;
  onMonthsChange: (value: number) => void;
}

export default function PriceCalculator({
  teams,
  months,
  onTeamsChange,
  onMonthsChange,
}: PriceCalculatorProps) {
  return (
    <div className={styles.container}>
      <span className={styles.label}>월세 자동 계산기</span>

      {/* 팀수 선택 */}
      <div className={styles.selectGroup}>
        <span className={styles.selectLabel}>같이 사용할 팀 수</span>
        <select
          className={styles.select}
          value={teams}
          onChange={(e) => onTeamsChange(Number(e.target.value))}
        >
          {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12].map((n) => (
            <option key={n} value={n}>
              {n} 팀
            </option>
          ))}
        </select>
      </div>

      {/* 개월수 선택 */}
      <div className={styles.selectGroup}>
        <span className={styles.selectLabel}>계약 월 수</span>
        <select
          className={styles.select}
          value={months}
          onChange={(e) => onMonthsChange(Number(e.target.value))}
        >
          {MONTH_OPTIONS.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
      </div>
    </div>
  );
}
