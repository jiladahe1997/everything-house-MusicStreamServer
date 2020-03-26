const search = require('../module/search')
const song_url = require('../module/song_url')
const axios = require('axios')
const fs = require('fs')
const fsPromises = fs.promises;


function sendMQ(){
  
}
module.exports = async (query, Request) => {
  const searchRes = await search(query, Request)
  const urlRes = await song_url({...query, id: searchRes.body.result.songs[0].id}, Request)
  const rawFile = await axios.get(urlRes.body.data[0].url, {
    responseType: 'arraybuffer'
  })
  await fsPromises.writeFile(`./${searchRes.body.result.songs[0].name}.mp3`, rawFile.data)

}