import * as React from 'react';

import {
  toast,
} from 'react-toastify';

import { Icon } from '@mdi/react';

import {
  mdiCommentAlertOutline,
  mdiCommentTextOutline,
  mdiCommentRemoveOutline,
} from '@mdi/js';

import {
  iconFunc
} from 'model/types';

import {
  EnumMessageLevel,
} from 'model/message';

import ToastTemplate from 'components/toast-template';

const defaultInfoIcon: iconFunc = () => (<Icon path={mdiCommentTextOutline} size="3rem" />);
const defaultWarnIcon: iconFunc = () => (<Icon path={mdiCommentAlertOutline} size="3rem" />);
const defaultErrorIcon: iconFunc = () => (<Icon path={mdiCommentRemoveOutline} size="3rem" />);


const displayText = (text: string, level = EnumMessageLevel.ERROR, icon?: iconFunc, autoClose: number = 5000): void => {
  toast.dismiss();

  switch (level) {
    case EnumMessageLevel.WARN:
      if (React.isValidElement(text)) {
        toast.warn(text, { autoClose });
      } else {
        toast.warn(
          <ToastTemplate icon={icon ? icon : defaultWarnIcon} text={text} />,
          { autoClose },
        );
      }
      break;
    case EnumMessageLevel.INFO:
      if (React.isValidElement(text)) {
        toast.info(text, { autoClose });
      } else {
        toast.info(
          <ToastTemplate icon={icon ? icon : defaultInfoIcon} text={text} />,
          { autoClose },
        );
      }
      break;
    default:
      if (React.isValidElement(text)) {
        toast.error(text, { autoClose });
      } else {
        toast.error(
          <ToastTemplate icon={icon ? icon : defaultErrorIcon} text={text} />,
          { autoClose },
        );
      }
      break;
  }
};

const displayHtml = (html: string | React.ReactNode, level = EnumMessageLevel.ERROR, icon?: iconFunc, autoClose: number = 5000): void => {
  toast.dismiss();

  switch (level) {
    case EnumMessageLevel.WARN:
      toast.warn(
        <ToastTemplate icon={icon ? icon : defaultWarnIcon} html={html} />,
        { autoClose },
      );
      break;
    case EnumMessageLevel.INFO:
      toast.info(
        <ToastTemplate icon={icon ? icon : defaultInfoIcon} html={html} />,
        { autoClose },
      );
      break;
    default:
      toast.error(
        <ToastTemplate icon={icon ? icon : defaultErrorIcon} html={html} />,
        { autoClose },
      );
      break;
  }
};

const error = (text: string, icon?: iconFunc, autoClose: number = 5000) => {
  displayText(text, EnumMessageLevel.ERROR, icon, autoClose);
};

const errorHtml = (html: string | React.ReactNode, icon?: iconFunc, autoClose: number = 5000) => {
  displayHtml(html, EnumMessageLevel.ERROR, icon, autoClose);
};

const warn = (text: string, icon?: iconFunc, autoClose: number = 5000) => {
  displayText(text, EnumMessageLevel.WARN, icon, autoClose);
};

const warnHtml = (html: string | React.ReactNode, icon?: iconFunc, autoClose: number = 5000) => {
  displayHtml(html, EnumMessageLevel.WARN, icon, autoClose);
};

const info = (text: string, icon?: iconFunc, autoClose: number = 5000) => {
  displayText(text, EnumMessageLevel.INFO, icon, autoClose);
};

const infoHtml = (html: string | React.ReactNode, icon?: iconFunc, autoClose: number = 5000) => {
  displayHtml(html, EnumMessageLevel.INFO, icon, autoClose);
};

const success = (text: string, icon?: iconFunc, autoClose: number = 5000) => {
  if (React.isValidElement(text)) {
    toast.success(text, {
      autoClose,
    });
  } else {
    toast.success(
      <ToastTemplate icon={icon ? icon : defaultErrorIcon} text={text} />,
      {
        autoClose
      },
    );
  }
};

export default {
  error,
  errorHtml,
  info,
  infoHtml,
  success,
  warn,
  warnHtml,
};
