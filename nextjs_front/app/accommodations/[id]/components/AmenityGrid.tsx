import React, { JSX } from 'react';
import styles from './AmenityGrid.module.css';
import {
  FaBed, FaTv, FaWifi, FaCar, FaFire, FaSnowflake,
  FaBlender, FaUtensils, FaDog, FaLock, FaBroom,
  FaCamera, FaWind
} from 'react-icons/fa';
import { MdIron, MdLocalLaundryService } from 'react-icons/md';
import { GiWashingMachine, GiBarbecue } from 'react-icons/gi';

interface AmenityGridProps {
  amenities: string;
}

const amenityIconMap: Record<string, JSX.Element> = {
  침대: <FaBed />,
  TV: <FaTv />,
  와이파이: <FaWifi />,
  주차: <FaCar />,
  난방: <FaFire />,
  에어컨: <FaSnowflake />,
  냉장고: <FaBlender />,
  식기도구: <FaUtensils />,
  전기밥솥: <FaUtensils />,
  가스레인지: <FaFire />,
  전자레인지: <FaBlender />,
  정수기: <FaWind />,
  세탁기: <GiWashingMachine />,
  바베큐: <GiBarbecue />,
  디지털도어락: <FaLock />,
  로봇청소기: <FaBroom />,
  청소도구: <FaBroom />,
  CCTV: <FaCamera />,
  헤어드라이어: <FaWind />,
  다리미: <MdIron />,
  건조기: <MdLocalLaundryService />,
  반려견: <FaDog />,
};

function getIcon(amenity: string): JSX.Element {
  const trimmed = amenity.trim();
  const matched = Object.entries(amenityIconMap).find(([key]) =>
    trimmed.includes(key)
  );
  return matched ? matched[1] : <FaUtensils />;
}

export default function AmenityGrid({ amenities }: AmenityGridProps) {
  if (!amenities) return null;

  const amenityList = amenities.split(',').map((a) => a.trim()).filter(Boolean);

  return (
    <section className={styles.section}>
      <h2 className={styles.sectionTitle}>구성용품</h2>
      <div className={styles.grid}>
        {amenityList.map((amenity, index) => (
          <div key={index} className={styles.item}>
            <span className={styles.icon}>{getIcon(amenity)}</span>
            <span className={styles.label}>{amenity}</span>
          </div>
        ))}
      </div>
    </section>
  );
}