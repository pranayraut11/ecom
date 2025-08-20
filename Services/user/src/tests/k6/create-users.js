import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 1,           // concurrent virtual users
  iterations: 300,    // total users to create (user1 ... user50)
};

export default function () {
  const createUrl = 'http://localhost:8088/users';  // change to your endpoint
  const userNumber = __ITER + 1; // __ITER starts from 0
  const username = `user${userNumber}`;
  const email = `${username}@example.com`;
  const password = '123';

  const payload = JSON.stringify({
    firstName: `First${userNumber}`,
    lastName: `Last${userNumber}`,
    email: email,
    username: username,
    password: password,
  });

  const params = {
    headers: { 'Content-Type': 'application/json',
                'X-Tenant-ID':'ecom'
             },
  };

  const res = http.post(createUrl, payload, params);

  check(res, {
    'user created': (r) => r.status === 201 || r.status === 200,
  });

  sleep(0.2);
}
