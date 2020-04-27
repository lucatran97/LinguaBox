const request = require('supertest')
const app = require('../app')
describe('SignIn Test Case 1', () => {
  it('Status should be success because input parameter is correct', async () => {
    const res = await request(app)
      .post('/users')
      .send({
        email: 'abc@temple.edu'
      })
    expect(res.body.status).toEqual('success')
  })
})

describe('SignIn Test Case 2', () => {
  it('Should fail because email is not in correct format', async () => {
    const res = await request(app)
      .post('/users')
      .send({
        email: 'abc'
      })
    expect(res.body.status).toEqual('failure')
  })
})

describe('SignIn Test Case 3', () => {
  it('Should fail because of missing parameter', async () => {
    const res = await request(app)
      .post('/users')
      .send({})
    expect(res.body.status).toEqual('failure')
  })
})