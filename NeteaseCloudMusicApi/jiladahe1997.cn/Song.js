const search = require('../module/search')
const song_url = require('../module/song_url')
const axios = require('axios')
const fs = require('fs')
const fsPromises = fs.promises;

async function downloadMp3(query, Request) {
  // 通知切歌服务器
  const searchRes = await search(query, Request)
  const urlRes = await song_url({...query, id: searchRes.body.result.songs[0].id}, Request)
  const rawFile = await axios.get(urlRes.body.data[0].url, {
    responseType: 'arraybuffer'
  })
  await fsPromises.writeFile(`C:/music/${searchRes.body.result.songs[0].name}.mp3`, rawFile.data)
  console.log(`${searchRes.body.result.songs[0].name}下载完毕`);
  sendMQ(searchRes.body.result.songs[0].name)
}

async function sendMQ(songName){
  const res = await axios.post('localhost:8080/music/finishDownLoad', {
    songName
  })
}

module.exports = async (query, Request) => {
  const searchRes = await search(query, Request)
  const res = await axios.post('localhost:8080/music/orderSong', {
    songName: searchRes.body.result.songs[0].name
  })

  if(res.msg === 'not found') {
    downloadMp3(query, Request)
    return {
      status: 200,
      body: {
        code: 0,
        msg: '点歌成功',
        data: null
      }
    }
  } else {
    return {
      status: 200,
      body: {
        code: 0,
        msg: res.msg,
        data: null
      }
    }
  }
}