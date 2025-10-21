import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */
  images: {
    // 外部画像ドメインの許可設定
    // Next.js Image コンポーネントで使用できる外部ドメインを指定
    remotePatterns: [
      {
        protocol: 'https',
        hostname: 'images.unsplash.com',
        port: '',
        pathname: '/**',
      },
      // 他の外部画像ドメインも追加可能
      // {
      //   protocol: 'https',
      //   hostname: 'example.com',
      //   port: '',
      //   pathname: '/images/**',
      // },
    ],
  },
};

export default nextConfig;
