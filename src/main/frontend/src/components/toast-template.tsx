import { EnumMessageLevel } from 'model/message';
import { iconFunc } from 'model/types';
import React from 'react';
import { FormattedMessage } from 'react-intl';

interface ToastTemplateProps {
  text?: string;
  icon?: iconFunc,
  padding: number;
  level: string;
  html?: string | React.ReactNode;
}

class ToastTemplate extends React.Component<ToastTemplateProps> {

  static defaultProps = {
    padding: 0,
    level: EnumMessageLevel.ERROR,
  }

  render() {
    return (
      <div>
        <table>
          <tbody>
            <tr style={{ verticalAlign: 'top' }}>
              {this.props.icon &&
                <td style={{ padding: this.props.padding, position: 'relative', top: -4 }}>
                  {this.props.icon()}
                </td>
              }
              {!this.props.html &&
                <td style={{ padding: (this.props.padding) + 4 }}><FormattedMessage id={this.props.text} defaultMessage={this.props.text} /></td>
              }
              {this.props.html &&
                <td style={{ padding: (this.props.padding) + 4 }}>
                  {this.props.html}
                </td>
              }
            </tr>
          </tbody>
        </table>
      </div >
    );
  }
}

export default ToastTemplate;
