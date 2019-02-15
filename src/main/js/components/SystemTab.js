const React = require('react');
const ReactDOM = require('react-dom');
import {  Button, Modal, Dropdown, Label, List } from 'semantic-ui-react'

import axios from "axios";



class SystemTab extends React.Component {

	constructor(props) {
		super(props);
		this.state = {config: {}, question: false};
	}
	
	componentDidMount() {
	    axios.get('/api/config')
            .then(response => {
                let newState = Object.assign({}, this.state);
                newState.config = response.data;
                this.setState(newState);
            });
	}

	onPlatformChange = (e, data) => {
        this.setState({ question: true, platform: data.value });
    }

    onPlatformChangeConfirmed = (e, data) => {
        const payload = {
            platform: this.state.platform
        };

        axios.put('/api/config', JSON.stringify(payload),{
            headers: {
                'Content-Type': 'application/json',
            }
        }).then(response => {
            let newState = Object.assign({}, this.state);
            newState.config = response.data;
            this.setState(newState);
        });
        this.setState({ question: false })
    }

    close = () => {
        this.setState({ question: false })
    }

	render() {
	    let { question, config } = this.state;
        let stateOptions = [];
        let selected = '';

        if (config.available) {
            config.available.map(mode => {
                if (config.platformID && mode == config.platformID.platformType) {
                    selected = mode;
                }
                stateOptions.push(
                        { key: mode, value: mode, text: mode }
                );
            })
        }

        return (
            <div>
                <Modal size='tiny' open={question} onClose={this.close}>
                    <Modal.Header>Change Platform</Modal.Header>
                        <Modal.Content>
                            <p>Are you sure you want to change platform</p>
                        </Modal.Content>
                    <Modal.Actions>
                    <Button negative onClick={this.close}>No</Button>
                    <Button positive onClick={this.onPlatformChangeConfirmed}>Yes</Button>
                    </Modal.Actions>
                </Modal>
                <List divided selection>
                     <List.Item>
                        <Label horizontal>
                            Current platform:
                        </Label>
                        <Dropdown selection value={selected} options={stateOptions} onChange={this.onPlatformChange} />
                    </List.Item>
                    <List.Item>
                        <Label horizontal>
                            Platform Name:
                        </Label>
                        {config.platformName}
                    </List.Item>
                </List>
            </div>
        )

	}
}

export default (SystemTab);