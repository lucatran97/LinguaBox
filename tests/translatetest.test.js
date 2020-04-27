const request = require('supertest')
const app = require('../app')
describe('Translate Test Case 1', () => {
  it('Should succeed because input parameters are correct', async () => {
    const res = await request(app)
      .post('/translate')
      .send({
        message: 'como estas?',
        language_from: 'es',
        language_to: 'en'
      })
    expect(res.body.status).toEqual('success')
  })
})

describe('Chat Test Case 2', () => {
    it('Should fail because original language code is missing', async () => {
      const res = await request(app)
      .post('/translate')
      .send({
        message: 'como estas?',
        language_to: 'en'
      })
      expect(res.body.status).toEqual('failure')
    })
  })

describe('Chat Test Case 2', () => {
  it('Should fail because message is missing', async () => {
    const res = await request(app)
    .post('/translate')
      .send({
        language_from: 'es',
        language_to: 'en'
      })
    expect(res.body.status).toEqual('failure')
  })
})

describe('Chat Test Case 3', () => {
  it('Should fail because target language code is missing', async () => {
    const res = await request(app)
    .post('/translate')
    .send({
      message: 'como estas?',
      language_from: 'es'
    })
    expect(res.body.status).toEqual('failure')
  })
})