import React from 'react';
import zxcvbn from 'zxcvbn';

// Material UI
import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';

const styles = (theme: Theme) => createStyles({
  weak: {
    background: '#d50000',
    color: 'white',
    padding: '2px 2px 2px 3px',
  },
  good: {
    background: '#FFD600',
    color: 'white',
    padding: '2px 2px 2px 3px',
  },
  strong: {
    background: '#2E7D32',
    color: 'white',
    padding: '2px 2px 2px 3px',
  },
});

interface PasswordStrengthProps extends WithStyles<typeof styles> {
  className?: string;
  password: string;
  minStrength: number;
  thresholdLength: number;
}

interface PasswordStrengthState {
  strength: number;
}

class PasswordStrength extends React.Component<PasswordStrengthProps, PasswordStrengthState> {

  state: PasswordStrengthState = {
    strength: 0,
  }

  componentDidUpdate(prevProps: PasswordStrengthProps) {
    if (this.props.password !== prevProps.password) {
      this.setState({
        strength: zxcvbn(this.props.password).score,
      });
    }
  }

  render() {
    const { classes, className, minStrength, thresholdLength, password } = this.props;
    const { strength } = this.state;

    const passwordLength = password.length;
    const passwordStrong = strength >= minStrength;
    const passwordLong = passwordLength > thresholdLength;

    const counterClass = passwordLength ? passwordLong ? passwordStrong ? classes.strong : classes.good : classes.weak : '';

    return (
      <div className={`${className}`}>
        <span className={counterClass}>{passwordLength ? passwordLong ? `${thresholdLength}+` : passwordLength : ''}</span>
      </div>
    );
  }
}

// Apply styles
const styledComponent = withStyles(styles)(PasswordStrength);

export default styledComponent;
