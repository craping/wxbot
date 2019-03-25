import Cookies from 'js-cookie'
import config from '@/config'
const { cookieExpires } = config

export const TOKEN_KEY = 'token'

export const setToken = (token) => {
  Cookies.set(TOKEN_KEY, token, {
    expires: cookieExpires || 1
  })
}

export const getToken = () => {
  const token = Cookies.get(TOKEN_KEY)
  if (token) return token
  else return false
}

export const delToken = () => {
  Cookies.remove(TOKEN_KEY);
}

/**
 * 匹配数据
 * @param {数据} data 
 * @param {*} argumentObj 
 */
export const filterAll = (data, argumentObj) => {
  return data.filter(d => {
    for (let argu in argumentObj) {
      if (d[argu] != null && d[argu].toUpperCase().includes(argumentObj[argu].toUpperCase()))
        return true;
    }
    return false;
  });
}

export const filter = (data, argumentObj) => {
  let res = data;
  let dataClone = data;
  for (let argu in argumentObj) {
    if (argumentObj[argu].length > 0) {
      res = dataClone.filter(d => {
        return d[argu].includes(argumentObj[argu]);
      });
      dataClone = res;
    }
  }
  return res;
}

/**
 * 获取content类型描述
 * @param {int} type 
 */
export const getContentTypeDesc = (type) => {
  let desc = '未知';
  switch (type) {
    case 1:
      desc = '文本';
      break
    case 2:
      desc = '图片';
      break
    case 3:
      desc = '表情';
      break
    case 4:
      desc = '视频';
      break
    case 5:
      desc = '文件';
      break
  }
  return desc;
}

/**
 * @param {String} url
 * @description 从URL中解析参数
 */
export const getParams = url => {
  const keyValueArr = url.split('?')[1].split('&')
  let paramObj = {}
  keyValueArr.forEach(item => {
    const keyValue = item.split('=')
    paramObj[keyValue[0]] = keyValue[1]
  })
  return paramObj
}

export const textareaToHtml = (txt) => {
  return txt.replace(/\r\n/g, '<br/>').replace(/\n/g, '<br/>').replace(/\s/g, ' ');
}

export const htmlToTextarea = (html) => {
  return html.replace(/<br\s*\/?>/ig, "\n");
}

export const formatDate = (value) => {
  let date = new Date(parseInt(value));
  let y = date.getFullYear();
  let MM = date.getMonth() + 1;
  MM = MM < 10 ? ('0' + MM) : MM;
  let d = date.getDate();
  d = d < 10 ? ('0' + d) : d;
  let h = date.getHours();
  h = h < 10 ? ('0' + h) : h;
  let m = date.getMinutes();
  m = m < 10 ? ('0' + m) : m;
  let s = date.getSeconds();
  s = s < 10 ? ('0' + s) : s;
  return y + '-' + MM + '-' + d + ' ' + h + ':' + m;
}