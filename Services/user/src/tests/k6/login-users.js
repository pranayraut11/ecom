import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
      { duration: "30s", target: 300 },  // ramp up to 300 users
      { duration: "2m", target: 300 },   // stay at 300 users
      { duration: "30s", target: 0 },    // ramp down
    ]   // must match number of created users
};

export default function () {
  const userNumber = __ITER + 1; // same numbering as create-users.js
  const username = `user${userNumber}`+'@example.com';
  const password = '123';

  const url = 'http://localhost:8088/auth/login'; // change to your login endpoint
  const payload = JSON.stringify({
    username: username,
    password: password,
  });

  const params = {
    headers: { 'Content-Type': 'application/json',
     'X-Tenant-ID':'ecom'},
  };

  const res = http.post(url, payload, params);

  check(res, {
    'login success': (r) => r.status === 200,
    'token received': (r) => r.json('token') !== undefined,
  });

  sleep(0.5);
}
