import React from 'react';

// Localization
import { injectIntl, IntlShape } from 'react-intl';

// Styles
import { createStyles, WithStyles } from '@material-ui/core';
import { Theme, withStyles } from '@material-ui/core/styles';

// Material UI
import Card from '@material-ui/core/Card';
import CardHeader from '@material-ui/core/CardHeader';
import CardContent from '@material-ui/core/CardContent';
import IconButton from '@material-ui/core/IconButton';

// Custom components
import FormatIndentDecreaseIcon from '@material-ui/icons/FormatIndentDecrease';
import FormatIndentIncreaseIcon from '@material-ui/icons/FormatIndentIncrease';

const styles = (theme: Theme) => createStyles({
  card: {
    width: '100%',
    borderRadius: 0,
    padding: theme.spacing(1),
    margin: theme.spacing(1),
  },
  text: {
    textAlign: 'justify',
  }
});

interface SectionComponentProps extends WithStyles<typeof styles> {
  intl: IntlShape;
  index: number;
}

interface SectionComponentState {
  indent: number;
}

class SectionComponent extends React.Component<SectionComponentProps, SectionComponentState> {

  constructor(props: SectionComponentProps) {
    super(props);

    this.state = { indent: 8 };
  }

  increaseIndent() {
    this.setState((state) => ({ indent: state.indent < 64 ? state.indent + 8 : state.indent }));
  }

  decreaseIndent() {
    this.setState((state) => ({ indent: state.indent > 0 ? state.indent - 8 : state.indent }));
  }

  render() {
    const _t = this.props.intl.formatMessage;

    const { classes, index } = this.props;

    return (
      <Card className={classes.card}>
        <CardHeader
          action={
            <div>
              <IconButton onClick={(e) => this.increaseIndent()} disabled={this.state.indent === 64}>
                <FormatIndentIncreaseIcon />
              </IconButton>
              <IconButton onClick={(e) => this.decreaseIndent()} disabled={this.state.indent === 0}>
                <FormatIndentDecreaseIcon />
              </IconButton>
            </div>
          }
          title={_t({ id: 'contract.master.section' }) + ` - ${index + 1}`}
        />
        <CardContent className={classes.text}>
          <p style={{ paddingLeft: this.state.indent }}>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean facilisis egestas gravida. Integer porttitor consectetur
            convallis. Phasellus non auctor felis. Donec vitae vulputate dui. In dui nibh, pellentesque vel pulvinar nec, finibus id
            mauris. Praesent consequat erat sed nulla elementum mollis. Nulla tempor non felis nec feugiat. Phasellus eget ex eget erat
            sagittis lobortis.
          </p>
        </CardContent>
      </Card>
    );
  }
}

// Apply styles
const styledComponent = withStyles(styles)(SectionComponent);

// Inject i18n resources
const localizedComponent = injectIntl(styledComponent);

// Export localized component
export default localizedComponent;
